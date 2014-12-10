package org.chilternquizleague.web

import java.util.logging.Level
import java.util.logging.Logger
import java.util.{List => JList}
import scala.collection.JavaConversions.asScalaBuffer
import scala.collection.JavaConversions.bufferAsJavaList
import scala.collection.JavaConversions.collectionAsScalaIterable
import scala.collection.JavaConversions.seqAsJavaList
import scala.collection.immutable.List
import scala.util.control.Exception.catching
import org.chilternquizleague.domain.BaseEntity
import org.chilternquizleague.domain.Competition
import org.chilternquizleague.domain.CompetitionType
import org.chilternquizleague.domain.Fixtures
import org.chilternquizleague.domain.GlobalApplicationData
import org.chilternquizleague.domain.Results
import org.chilternquizleague.domain.Season
import org.chilternquizleague.domain.Team
import org.chilternquizleague.domain.TeamCompetition
import org.chilternquizleague.domain.Text
import org.chilternquizleague.domain.User
import org.chilternquizleague.domain.individuals.IndividualQuiz
import org.chilternquizleague.results.ResultHandler
import org.chilternquizleague.util.HttpUtils.RequestImprovements
import org.chilternquizleague.util.StringUtils.StringImprovements
import org.chilternquizleague.views.CompetitionTypeView
import org.chilternquizleague.views.CompetitionView
import org.chilternquizleague.views.ContactSubmission
import org.chilternquizleague.views.LeagueTableView
import org.chilternquizleague.views.PreSubmissionView
import org.chilternquizleague.views.ResultsReportsView
import org.chilternquizleague.views.ResultSubmission
import org.chilternquizleague.views.SeasonView
import org.chilternquizleague.views.TeamExtras
import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.databind.JsonSerializer
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializerProvider
import com.fasterxml.jackson.databind.module.SimpleModule
import com.googlecode.objectify.Key
import com.googlecode.objectify.ObjectifyService.ofy
import javax.servlet.ServletConfig
import javax.servlet.http.HttpServlet
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse
import org.chilternquizleague.util.Storage.{entity => entityByKey}
import org.chilternquizleague.util.Storage.entityList
import org.chilternquizleague.util.Storage.save
import java.util.ArrayList
import org.chilternquizleague.views.GlobalApplicationDataView
import scala.collection.immutable.Iterable
import java.util.Date


trait BaseRest extends HttpServlet {

  class ScalaIterableSerialiser extends JsonSerializer[Iterable[Any]]{
    override def serialize(list:Iterable[Any], gen: JsonGenerator, prov: SerializerProvider):Unit = {
      gen writeStartArray;
      
      list foreach {gen.writeObject(_)}
      
      gen writeEndArray()
      
    }
  }
  
  val LOG: Logger = Logger.getLogger(classOf[BaseRest].getName());

  def aliases: Map[String, String]
  val packages: List[Package] = List(classOf[BaseEntity].getPackage(), classOf[IndividualQuiz].getPackage());
  val objectMapper: ObjectMapper = new ObjectMapper
  def parts(req: HttpServletRequest) = req.getPathInfo().split("\\/").tail;
  def entityFilter[T <: BaseEntity]: T => Boolean

  def entityName(head: String) = {
    val stripped = head.replace("-list", "")
    aliases.getOrElse(stripped, stripped)
  }

  def classFromPart[T](part: String) = {

    val className = part.substring(0, 1).toUpperCase() + part.substring(1)
    def fun(c: Option[Class[T]], p: Package): Option[Class[T]] = {
    	import scala.util.control.Exception._

      if (c.isDefined) c else catching(classOf[ClassNotFoundException]) opt Class.forName(p.getName() + "." + className).asInstanceOf[Class[T]]
    }
    packages.foldLeft(Option[Class[T]](null))(fun)
  }

  def handleEntities(parts: Seq[String], head: String): Option[_] = {

    val entName = entityName(head)
    head match {

      case e if e.`contains`("-list") => makeEntityList(entName).map(new ArrayList(_))
      case _ => entityByParam(parts.tail.head, entName)
    }

  }

  def makeEntityList[T <: BaseEntity](entityName: String):Option[List[T]] = classFromPart(entityName) flatMap { c:Class[T] => makeEntityList(c) }
  def makeEntityList[T <: BaseEntity](c:Class[T]):Option[List[T]] = Some(entityList(c).filter(entityFilter[T]))

  def entityByParam[T](idPart: String, entityName: String): Option[T] = classFromPart[T](entityName) flatMap { clazz: Class[T] => entityByKey(idPart.toLongOpt, clazz) }

  def saveUpdate[T <: BaseEntity](req: HttpServletRequest, entityName: String): T = {

    val retval = classFromPart[T](entityName) map { clazz =>
      {

        val e = objectMapper.readValue(req.getReader(), clazz)
        e.prePersist
        val key = save(e)

        ofy.load.key(key).now

      }
    }

    retval.getOrElse(throw new ClassNotFoundException(entityName))
  }

  def logJson[T](things: T, message: String = "") = {
    if (LOG.isLoggable(Level.FINE)) {
      LOG.fine(message + "\n" + things.getClass.getName() + " : " +objectMapper.writeValueAsString(things));
    }

    things
  }
  
   
}

class EntityService extends BaseRest {

  override val aliases = Map(("text", "globalText"), ("global", "GlobalApplicationData"))
  override def entityFilter[T] = { _ => true }

  override def doGet(req: HttpServletRequest, resp: HttpServletResponse) = {
    val bits = parts(req)
    val head = bits.head
    val item = head match {

      case "global" => entityByKey(Application.globalApplicationDataId, classOf[GlobalApplicationData])
      case "competitionType-list" => Some(CompetitionTypeView.getList)
      case _ => handleEntities(bits, head)

    }

    item.foreach(a => objectMapper.writeValue(resp.getWriter, logJson(a, "writing:")))

  }

  override def doPost(req: HttpServletRequest, resp: HttpServletResponse) = {
    val item: BaseEntity = saveUpdate(req, entityName(parts(req).head))

    objectMapper.writeValue(resp.getWriter, logJson(item, "out:"))
  }

  override def doPut(req: HttpServletRequest, resp: HttpServletResponse) = doPost(req, resp)

}

class ViewService extends BaseRest {

  override val aliases = Map[String, String]()
  override def entityFilter[T <: BaseEntity] = { !_.isRetired() }

  class UserSerializer extends JsonSerializer[User] {
    override def serialize(user: User, gen: JsonGenerator, prov: SerializerProvider) = {}
  }
  class TextSerializer extends JsonSerializer[Text] {
    override def serialize(text: Text, gen: JsonGenerator, prov: SerializerProvider) = {
      gen writeStartObject;
      gen writeNullField "text"
      gen writeEndObject
    }

  }

  override def init(config: ServletConfig) = {
    val module = new SimpleModule
    module.addSerializer(classOf[User], new UserSerializer)
    module.addSerializer(classOf[Text], new TextSerializer)
    module.addSerializer(classOf[Iterable[Any]], new ScalaIterableSerialiser)
    objectMapper registerModule module
  }

  override def doGet(req: HttpServletRequest, resp: HttpServletResponse) = {
    val bits = parts(req)
    val head = bits.head

    val item: Option[_] = head match {

      case a if a.contains("globaldata") => entityByKey(Application.globalApplicationDataId, classOf[GlobalApplicationData]).map(new GlobalApplicationDataView(_))
      case a if a.contains("leaguetable") => currentLeagueTable(req, CompetitionType.LEAGUE)
      case a if a.contains("beertable") => currentLeagueTable(req, CompetitionType.BEER)
      case a if a.contains("season-views") => seasons
      case a if a.contains("team-extras") => teamExtras(req)
      case a if a.contains("all-results") => allResults(req)
      case a if a.contains("competition-results") => competitionResults(req)
      case a if a.contains("competition-fixtures") => competitionFixtures(req)
      case a if a.contains("fixtures-for-email") => fixturesForEmail(req)
      case a if a.contains("competitions-view") => competitionsForSeason(req)
      case a if a.contains("text") => textForName(req)
      case a if a.contains("reports") => resultReports(req)
      case _ => handleEntities(bits, head)

    }

    item foreach { a => objectMapper.writeValue(resp.getWriter, logJson(a, "viewService writing:")) }

  }

  override def doPost(req: HttpServletRequest, resp: HttpServletResponse) = {

    val ret: Option[_] = req.getPathInfo() match {

      case a if a.endsWith("submit-results") => submitResults(req)
      case a if a.endsWith("submit-contact") => submitContact(req)
      case _ => None

    }

    ret.foreach(r => objectMapper.writeValue(resp.getWriter, r))

  }
  
  def seasons():Option[List[SeasonView]] = {makeEntityList(classOf[Season]) map { _ map {new SeasonView(_)}}}

  def submitResults(req: HttpServletRequest) = {

    val submissions = objectMapper.readValue(req.getReader(), classOf[Array[ResultSubmission]]);

    submissions.foreach(sub => ResultHandler(sub.getResult, sub.getEmail, sub.getSeasonId, sub.getCompetitionType))

    None
  }

  def submitContact(req: HttpServletRequest) = {
    val submission = objectMapper.readValue(
      req.getReader, classOf[ContactSubmission]);

    EmailSender(submission.getSender, submission.getRecipient, submission.getText);
    None
  }

  def currentLeagueTable(req: HttpServletRequest, compType: CompetitionType):Option[LeagueTableView] = entityByKey(idParam(req), classOf[Season]) map (a => new LeagueTableView(a, compType))

  def teamExtras(req: HttpServletRequest): Option[TeamExtras] = {

    val teamId = idParam(req, "teamId")

    val team = entityByKey(teamId, classOf[Team])

    team flatMap { t =>
      {

        entityByKey(idParam(req, "seasonId"), classOf[Season]) flatMap { s => Some(new TeamExtras(t, teamFixtures(teamId, s), teamResults(teamId, s))) }
      }
    }
  }

  def teamFixtures(teamId: Option[Long], season: Season, limit:Int = 20000, filter:Fixtures => Boolean = {_ => true}): List[Fixtures] = {

    val competitions = season.getTeamCompetitions.toList

    def flatMapFixtures(f: Fixtures): List[Fixtures] = {

      val newFix = new Fixtures(f);
      newFix.setFixtures(f.getFixtures.toList filter { f => (teamId contains f.getHome.getId) || (teamId contains f.getAway.getId()) })

      if (newFix.getFixtures.isEmpty) List() else List(newFix)
    }

    competitions filter { _ != null } filter { _.getType != CompetitionType.BEER } flatMap { _.getFixtures filter filter flatMap flatMapFixtures } sortWith(_ .getStart before  _.getStart) slice(0, limit)


  }

  def teamResults(teamId: Option[Long], season: Season, limit:Int = 20000, filter:Results => Boolean = {_ => true}): List[Results] = {
    val competitions = season.getTeamCompetitions.toList

    def flatMapResults(f: Results): List[Results] = {

      val newRes = new Results(f);
      newRes.setResults(f.getResults filter { f => (teamId contains f.getFixture.getHome.getId) || (teamId contains f.getFixture.getAway.getId) })

      if (newRes.getResults.isEmpty) List() else List(newRes)
    }

    competitions filter { _ != null } filter { _.getType != CompetitionType.BEER } flatMap { _.getResults filter filter flatMap flatMapResults } sortWith(_.getDate before _.getDate) slice(0,limit)

  }

  /**
   * Relies on http params id:Season and type:CompetitionType
   */
  private def teamCompetitionForSeason(req: HttpServletRequest):Option[TeamCompetition] = {
      val compType = req.parameter("type") map { t => CompetitionType.valueOf(t) }

    entityByKey(idParam(req), classOf[Season]) flatMap {
      s => compType map {
        t => s.getCompetition(t).asInstanceOf[TeamCompetition] }}
  }
  
  def competitionResults(req: HttpServletRequest): Option[JList[Results]] = {

    teamCompetitionForSeason(req) map {a:TeamCompetition => a.getResults()}
  }

  def competitionFixtures(req: HttpServletRequest): Option[JList[Fixtures]] = {

   teamCompetitionForSeason(req) map {a: TeamCompetition => a.getFixtures()}

  }

  def textForName(req: HttpServletRequest): Option[String] = {

    entityByKey(Application.globalApplicationDataId, classOf[GlobalApplicationData]).flatMap(g => { req.parameter("name") map { n => g.getGlobalText.getText(n) } })
  }

  def allResults(req: HttpServletRequest):Option[JList[_]] =
    Some(entityByKey(idParam(req), classOf[Season]).fold(List[TeamCompetition]())(_.getTeamCompetitions.toList) filter { !_.isSubsidiary() } flatMap { _.getResults })

  def fixturesForEmail(req: HttpServletRequest): Option[PreSubmissionView] = {

    val season = entityByKey(idParam(req, "seasonId"), classOf[Season])
    val email = req.parameter("email").map(_.trim())

    val teams = entityList(classOf[Team])

    email flatMap { e => teams.filter(!_.getUsers.filter(_.getEmail.equalsIgnoreCase(e)).isEmpty).foldLeft(Option[PreSubmissionView](null))((a, t) => season.map(s => new PreSubmissionView(t, teamFixtures(Some(t.getId), s), teamResults(Some(t.getId),s)))) }
  }

  def competitionsForSeason(req: HttpServletRequest): Option[JList[CompetitionView]] =
    entityByKey(idParam(req), classOf[Season]).map(_.getCompetitions.values.toList.map { a: Competition => new CompetitionView(a) })

  def resultReports(req: HttpServletRequest): Option[ResultsReportsView] = {

    val team = entityByKey(idParam(req, "homeTeamId"), classOf[Team])
    val results = req.parameter("resultsKey").map(a => (ofy.load.key(Key.create(a)).now.asInstanceOf[Results]))

    team.flatMap(t => results.map(r => new ResultsReportsView(r.findRow(t))))
  }

  protected def idParam(req: HttpServletRequest, name: String = "id") = req parameter (name) flatMap { _ toLongOpt }

}