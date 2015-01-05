package org.chilternquizleague.web

import java.util.logging.Level
import java.util.logging.Logger
import java.util.{List => JList}
import scala.collection.JavaConversions._
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
import org.chilternquizleague.domain.util.RefUtils._
import org.chilternquizleague.results.ResultHandler
import org.chilternquizleague.util.HttpUtils.RequestImprovements
import org.chilternquizleague.util.StringUtils.StringImprovements
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
import org.chilternquizleague.util.ClassUtils._
import java.util.ArrayList
import scala.collection.immutable.Iterable
import java.util.Date
import com.googlecode.objectify.Ref
import com.fasterxml.jackson.databind.JsonDeserializer
import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.core.SerializableString
import com.fasterxml.jackson.core.io.SerializedString
import com.fasterxml.jackson.databind.JsonNode
import java.io.StringWriter
import com.google.api.client.util.StringUtils
import org.apache.commons.io.IOUtils
import org.chilternquizleague.domain.BaseLeagueCompetition
import org.chilternquizleague.util.JacksonUtils
import org.chilternquizleague.domain.Statistics


trait BaseRest extends HttpServlet {


  
  val LOG: Logger = Logger.getLogger(classOf[BaseRest].getName());

  def aliases: Map[String, String]
  val objectMapper: ObjectMapper = new ObjectMapper
  def parts(req: HttpServletRequest) = req.getPathInfo().split("\\/").tail;
  def entityFilter[T <: BaseEntity]: T => Boolean

  def entityName(head: String) = {
    val stripped = head.replace("-list", "")
    aliases.getOrElse(stripped, stripped)
  }

  def handleEntities(parts: Seq[String], head: String): Option[_] = {

    val entName = entityName(head)
    head match {

      case e if e.`contains`("-list") => makeEntityList(entName).map(new ArrayList(_))
      case _ => entityByParam(parts.tail.head, entName)
    }

  }

  protected def idParam(req: HttpServletRequest, name: String = "id") = req id name

  def makeEntityList[T <: BaseEntity](entityName: String):Option[List[T]] = classFromPart(entityName) flatMap { c:Class[T] => makeEntityList(c) }
  def makeEntityList[T <: BaseEntity](c:Class[T]):Option[List[T]] = Some(entityList(c).filter(entityFilter[T]))

  def entityByParam[T](idPart: String, entityName: String): Option[T] = classFromPart[T](entityName) flatMap { clazz: Class[T] => entityByKey(idPart.toLongOpt, clazz) }

  def saveUpdate[T <: BaseEntity](req: HttpServletRequest, entityName: String): T = {

    val retval = classFromPart[T](entityName) map { clazz =>
      ofy.load.key(save(objectMapper.readValue(req.getReader(), clazz))).now
    }
    retval.getOrElse(throw new ClassNotFoundException(entityName))
  }

  def logJson[T](things: T, message: String = "") = {
    if (LOG.isLoggable(Level.FINE)) {
      LOG.fine(s"$message\n${things.getClass.getName} : ${objectMapper.writeValueAsString(things)}")
    }

    things
  }
  
   
}

class EntityService extends BaseRest {

  override val aliases = Map(("text", "CommonText"), ("global", "GlobalApplicationData"))
  override def entityFilter[T] = { _ => true }

  override def init(config: ServletConfig) = {
    objectMapper registerModule JacksonUtils.unsafeModule
  }
  
  override def doGet(req: HttpServletRequest, resp: HttpServletResponse) = {
    val bits = parts(req)
    val head = bits.head
    val item = head match {

      case "global" => Application.globalData
      case "competitionType-list" => Some(CompetitionTypeView.list)
      case _ => handleEntities(bits, head)

    }

    item.foreach(a => objectMapper.writeValue(resp.getWriter, logJson(a, "writing:")))

  }
  
  def rebuildStats(req:HttpServletRequest) = for( s <- entityByKey(req.id("seasonId"), classOf[Season])) yield HistoricalStatsAggregator.perform(s)
  
  override def doPost(req: HttpServletRequest, resp: HttpServletResponse) = {
    val bits = parts(req)
    val head = bits.head
    val item:Option[Any] = head match {
          case "rebuild-stats" => rebuildStats(req)
          case _ => Option(saveUpdate(req, entityName(parts(req).head)))
    }

    item.foreach(i=>objectMapper.writeValue(resp.getWriter, logJson(i, "out:")))
  }

  override def doPut(req: HttpServletRequest, resp: HttpServletResponse) = doPost(req, resp)

}

class ViewService extends BaseRest {

  override val aliases = Map(("GlobalText","CommonText"))
  override def entityFilter[T <: BaseEntity] = { !_.retired }



  override def init(config: ServletConfig) = {
    objectMapper registerModule JacksonUtils.safeModule
  }

  override def doGet(req: HttpServletRequest, resp: HttpServletResponse) = {
    val bits = parts(req)
    val head = bits.head
    val stripped = head.replace("-list", "")

    val item: Option[_] = stripped match {

      case "globaldata" => Application.globalData.map(new GlobalApplicationDataView(_))
      case "leaguetable" => currentLeagueTable(req, CompetitionType.LEAGUE)
      case "beertable" => currentLeagueTable(req, CompetitionType.BEER)
      case "season-views" => seasons
      case "team-extras" => teamExtras(req)
      case "all-results" => allResults(req)
      case "all-fixtures" => allFixtures(req)
      case "competition-results" => competitionResults(req)
      case "competition-fixtures" => competitionFixtures(req)
      case "fixtures-for-email" => fixturesForEmail(req)
      case "competitions-view" => competitionsForSeason(req)
      case "text" => textForName(req)
      case "reports" => resultReports(req)
      case "team-statistics" => teamStatistics(req)
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
  
  def teamStatistics(req: HttpServletRequest) = {
    
    for{
      t <- entityByKey(req.id("teamId"), classOf[Team])
      s <- entityByKey(req.id("seasonId"), classOf[Season])
      stats = entityList(classOf[Statistics], ("team",t), ("season",s))
    } yield{
      stats match {
        case Nil => null
        case _ => stats.map(new StatisticsView(_)).head
      }
   } 
  }
  
  def seasons():Option[List[SeasonView]] = makeEntityList(classOf[Season]) map { _ map {new SeasonView(_)}}

  def submitResults(req: HttpServletRequest) = {

    val submissions = objectMapper.readValue(req.getReader(), classOf[Array[ResultSubmission]]);

    submissions.foreach(sub => ResultHandler(sub.result, sub.email, sub.seasonId, sub.competitionType))

    Some("")
  }

  def submitContact(req: HttpServletRequest) = {
    val submission = objectMapper.readValue(
      req.getReader, classOf[ContactSubmission]);

    EmailSender(submission.sender, submission.recipient, submission.text);
    None
  }

  def currentLeagueTable(req: HttpServletRequest, compType: CompetitionType):Option[LeagueTableView] = entityByKey(idParam(req), classOf[Season]) map (a => new LeagueTableView(a, compType))

  def teamExtras(req: HttpServletRequest): Option[TeamExtras] = {

    val teamId = idParam(req, "teamId")
    for{
      t <- entityByKey(teamId, classOf[Team])
      s <- entityByKey(idParam(req, "seasonId"), classOf[Season])
    }
    yield{
      new TeamExtras(t, teamFixtures(teamId, s), teamResults(teamId, s))
    }
 }

  def teamFixtures(teamId: Option[Long], season: Season, limit:Int = 20000, filter:Fixtures => Boolean = {_ => true}): List[Fixtures] = {

    val competitions = season.teamCompetitions

    def flatMapFixtures(f: Fixtures): List[Fixtures] = {

      val newFix = Fixtures(f);
      newFix.fixtures = f.fixtures.toList filter { f => (teamId contains f.home.id) || (teamId contains f.away.id) }

      if (newFix.fixtures.isEmpty) Nil else List(newFix)
    }

    competitions filter { _ != null } filter { _.`type` != CompetitionType.BEER } flatMap { _.fixtures.map(_.get) filter filter flatMap flatMapFixtures } sortWith(_ .start before  _.start) slice(0, limit)


  }

  def teamResults(teamId: Option[Long], season: Season, limit:Int = 20000, filter:Results => Boolean = {_ => true}): List[Results] = {
    val competitions = season.teamCompetitions

    def flatMapResults(f: Results): List[Results] = {

      val newRes = Results(f);
      newRes.results = f.results filter { f => (teamId contains f.fixture.home.id) || (teamId contains f.fixture.away.id) }

      if (newRes.results.isEmpty) Nil else List(newRes)
    }

    competitions filter { _ != null } filter { _.`type` != CompetitionType.BEER } flatMap { _.results.map(_.get) filter(_!=null) filter filter flatMap flatMapResults } sortWith(_.date before _.date) slice(0,limit)

  }

  /**
   * Relies on http params id:Season and type:CompetitionType
   */
  private def teamCompetitionForSeason(req: HttpServletRequest):Option[TeamCompetition] = {
      val compType = req.parameter("type") map { t => CompetitionType.valueOf(t) }

    entityByKey(idParam(req), classOf[Season]) flatMap {
      s => compType map {
        t => s.competition(t).asInstanceOf[TeamCompetition] }}
  }
  
  def competitionResults(req: HttpServletRequest): Option[JList[Results]] = teamCompetitionForSeason(req) map {_.results}

  def competitionFixtures(req: HttpServletRequest): Option[JList[Fixtures]] = teamCompetitionForSeason(req) map {_.fixtures}



  def textForName(req: HttpServletRequest): Option[String] = {

    Application.globalData.flatMap(g => { req.parameter("name") map { n => g.globalText.text(n) } })
  }

  def allResults(req: HttpServletRequest):Option[JList[_]] = 
    entityByKey(idParam(req), classOf[Season]).map(_.teamCompetitions filter { !_.subsidiary } flatMap { _.results })
  
  def allFixtures(req: HttpServletRequest):Option[JList[_]] =
    entityByKey(idParam(req), classOf[Season]).map(_.teamCompetitions filter { !_.subsidiary } flatMap { _.fixtures })

    
  def fixturesForEmail(req: HttpServletRequest): Option[PreSubmissionView] = {
   
    for{
      e <- req.parameter("email").map(_.trim())
      t <- entityList(classOf[Team]).find(_.users.exists(_.email equalsIgnoreCase e))
      s <- entityByKey(req.id("seasonId"), classOf[Season])
    }
    yield{
      new PreSubmissionView(t, teamFixtures(Some(t.id), s), teamResults(Some(t.id),s))
    } 
  }

  def competitionsForSeason(req: HttpServletRequest): Option[JList[CompetitionView]] =
		  entityByKey(idParam(req), classOf[Season]).map(_.competitions.values.toList.map { a => new CompetitionView(a) })
  
  def resultReports(req: HttpServletRequest): Option[ResultsReportsView] = {
    
    for{
      t <- entityByKey(idParam(req, "homeTeamId"), classOf[Team])
      key <- req.parameter("resultsKey")
      r <- Option(ofy.load.key(Key.create(key)).now.asInstanceOf[Results])
      reps <- r.findRow(t)
    }
    yield{
      new ResultsReportsView(reps) 
    }
  }


}