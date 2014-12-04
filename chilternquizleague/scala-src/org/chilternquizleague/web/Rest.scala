package org.chilternquizleague.web

import javax.servlet.http.HttpServlet
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse
import scala.collection.immutable.List
import org.chilternquizleague.domain.BaseEntity
import org.chilternquizleague.domain.individuals.IndividualQuiz
import java.util.logging.Logger
import com.fasterxml.jackson.databind.ObjectMapper
import com.googlecode.objectify.ObjectifyService.ofy
import java.util.logging.Level
import scala.collection.JavaConversions._
import org.chilternquizleague.util.StringUtils._
import org.chilternquizleague.util.HttpUtils._
import com.googlecode.objectify.Key
import org.chilternquizleague.views.CompetitionTypeView
import org.chilternquizleague.domain.GlobalApplicationData
import com.fasterxml.jackson.databind.JsonSerializer
import org.chilternquizleague.domain.User
import com.fasterxml.jackson.databind.SerializerProvider
import com.fasterxml.jackson.core.JsonGenerator
import org.chilternquizleague.domain.Text
import javax.servlet.ServletConfig
import com.fasterxml.jackson.databind.module.SimpleModule
import org.chilternquizleague.domain.Season
import org.chilternquizleague.views.LeagueTableView
import org.chilternquizleague.domain.CompetitionType
import scala.collection.immutable.Iterable
import org.chilternquizleague.views.SeasonView
import org.chilternquizleague.domain.Team
import org.chilternquizleague.views.TeamExtras
import org.chilternquizleague.domain.Fixtures
import org.chilternquizleague.domain.Results
import org.chilternquizleague.domain.Competition
import org.chilternquizleague.domain.TeamCompetition
import org.chilternquizleague.views.PreSubmissionView
import org.chilternquizleague.views.CompetitionView
import org.chilternquizleague.views.ResultsReportsView
import java.util.ArrayList
import org.chilternquizleague.views.ResultSubmission
import org.chilternquizleague.results.ResultHandler
import org.chilternquizleague.views.ContactSubmission
import org.chilternquizleague.contact.EmailSender

trait BaseRest extends HttpServlet {

  val LOG: Logger = Logger.getLogger(classOf[BaseRESTService].getName());

  def aliases: Map[String, String]
  val packages: List[Package] = List(classOf[BaseEntity].getPackage(), classOf[IndividualQuiz].getPackage());
  def objectMapper: ObjectMapper = new ObjectMapper
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

      case e if e.`contains`("-list") => makeEntityList(entName)
      case _ => entityByKey(parts.tail.head, entName)
    }

  }

  def makeEntityList[T <: BaseEntity](entityName: String) = Some(classFromPart(entityName).fold(new ArrayList[T]())((c: Class[T]) => (new ArrayList[T](ofy.load().`type`(c).list.filter(entityFilter[T])))))

  def entityByKey[T](idPart: String, entityName: String): Option[T] = classFromPart[T](entityName) flatMap { clazz: Class[T] => entityByKey(idPart.toLongOpt, clazz) }

  def entityByKey[T](id: Option[Long], clazz: Class[T]): Option[T] = {

    Option[T](id match {

      case Some(idval) => ofy.load.now(Key.create(clazz, idval))
      case None => clazz.newInstance
    })

  }

  def saveUpdate[T <: BaseEntity](req: HttpServletRequest, entityName: String): T = {

    val retval = classFromPart[T](entityName) map { clazz =>
      {

        val e = objectMapper.readValue(req.getReader(), clazz)
        e.prePersist
        val key = ofy.save.entity(e).now

        ofy.load.key(key).now

      }
    }

    retval.getOrElse(throw new ClassNotFoundException(entityName))
  }

  def logJson[T](things: T, message: String = "") = {
    if (LOG.isLoggable(Level.FINE)) {
      LOG.fine(message + "\n" + objectMapper.writeValueAsString(things));
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

      case "global" => entityByKey(Some(AppStartListener.globalApplicationDataId), classOf[GlobalApplicationData])
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
    objectMapper registerModule module
  }

  override def doGet(req: HttpServletRequest, resp: HttpServletResponse) = {
    val bits = parts(req)
    val head = bits.head

    val item: Option[_] = head match {

      case a if a.contains("globaldata") => Option(ofy().load().now(
        Key.create(classOf[GlobalApplicationData],
          AppStartListener.globalApplicationDataId)))
      case a if a.contains("leaguetable") => currentLeagueTable(req, CompetitionType.LEAGUE)
      case a if a.contains("beertable") => currentLeagueTable(req, CompetitionType.BEER)
      case a if a.contains("season-views") => Some(ofy.load.`type`(classOf[Season]).map(new SeasonView(_)))
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

    item foreach { a => objectMapper.writeValue(resp.getWriter, logJson(item, "writing:")) }

  }

  override def doPost(req: HttpServletRequest, resp: HttpServletResponse) = {

    val ret: Option[_] = req.getPathInfo() match {

      case a if a.endsWith("submit-results") => submitResults(req)
      case a if a.endsWith("submit-contact") => submitContact(req)
      case _ => None

    }

    ret.foreach(r => objectMapper.writeValue(resp.getWriter, r))

  }

  def submitResults(req: HttpServletRequest) = {

    val submissions = objectMapper.readValue(req.getReader(), classOf[Array[ResultSubmission]]);

    submissions.foreach(sub => new ResultHandler(sub.getResult, sub.getEmail, sub.getSeasonId, sub.getCompetitionType).commit)

    None
  }

  def submitContact(req: HttpServletRequest) = {
    val submission = objectMapper.readValue(
      req.getReader, classOf[ContactSubmission]);

    new EmailSender().sendMail(submission.getSender, submission.getRecipient, submission.getText);
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

  def teamFixtures(teamId: Option[Long], season: Season): List[Fixtures] = {

    val competitions = season.getTeamCompetitions.toList

    def flatMapFixtures(f: Fixtures): List[Fixtures] = {

      val newFix = new Fixtures(f);
      newFix.setFixtures(f.getFixtures.toList filter { f => (teamId contains f.getHome.getId) || (teamId contains f.getAway.getId()) })

      if (newFix.getFixtures.isEmpty) List() else List(newFix)
    }

    competitions filter { _ != null } filter { _.getType != CompetitionType.BEER } flatMap { _.getFixtures flatMap flatMapFixtures }

  }

  def teamResults(teamId: Option[Long], season: Season): List[Results] = {
    val competitions = season.getTeamCompetitions.toList

    def flatMapResults(f: Results): List[Results] = {

      val newRes = new Results(f);
      newRes.setResults(f.getResults filter { f => (teamId contains f.getFixture.getHome.getId) || (teamId contains f.getFixture.getAway.getId) })

      if (newRes.getResults.isEmpty) List() else List(newRes)
    }

    competitions filter { _ != null } filter { _.getType != CompetitionType.BEER } flatMap { _.getResults flatMap flatMapResults }

  }

  def competitionResults(req: HttpServletRequest): Option[List[Results]] = {

    val compType = req.parameter("type") map { t => CompetitionType.valueOf(t) }

    entityByKey(idParam(req), classOf[Season]) flatMap { s => compType map { t => s.getCompetition(t) } map { a: TeamCompetition => a.getResults().toList } }

  }

  def competitionFixtures(req: HttpServletRequest): Option[List[Fixtures]] = {

    val compType = req.parameter("type") map { t => CompetitionType.valueOf(t) }

    entityByKey(idParam(req), classOf[Season]) flatMap { s => compType map { t => s.getCompetition(t) } map { a: TeamCompetition => a.getFixtures().toList } }

  }

  def textForName(req: HttpServletRequest): Option[String] = {

    entityByKey(Option(AppStartListener.globalApplicationDataId), classOf[GlobalApplicationData]).flatMap(g => { req.parameter("name") map { n => g.getGlobalText().getText(n) } })
  }

  def allResults(req: HttpServletRequest) =
    Some(entityByKey(idParam(req), classOf[Season]).fold(List[TeamCompetition]())(_.getTeamCompetitions.toList) filter { !_.isSubsidiary() } flatMap { _.getResults })

  def fixturesForEmail(req: HttpServletRequest): Option[PreSubmissionView] = {

    val season = entityByKey(idParam(req, "seasonId"), classOf[Season])
    val email = req.parameter("email").map(_.trim())

    val teams = ofy.load.`type`(classOf[Team]).list()

    email flatMap { e => teams.filter(!_.getUsers.filter(!_.getEmail.equalsIgnoreCase(e)).isEmpty).foldLeft(Option[PreSubmissionView](null))((a, t) => season.map(s => new PreSubmissionView(t, teamFixtures(Some(t.getId()), s)))) }
  }

  def competitionsForSeason(req: HttpServletRequest): Option[List[CompetitionView]] =
    entityByKey(idParam(req), classOf[Season]).map(_.getCompetitions.values.toList.map { a: Competition => new CompetitionView(a) })

  def resultReports(req: HttpServletRequest): Option[ResultsReportsView] = {

    val team = entityByKey(idParam(req, "homeTeamId"), classOf[Team])
    val results = req.parameter("resultsKey").map(a => (ofy.load.key(Key.create(a)).now.asInstanceOf[Results]))

    team.flatMap(t => results.map(r => new ResultsReportsView(r.findRow(t))))
  }

  protected def idParam(req: HttpServletRequest, name: String = "id") = req parameter (name) flatMap { _ toLongOpt }

}