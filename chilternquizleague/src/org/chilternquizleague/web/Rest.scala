package org.chilternquizleague.web

import java.util.logging.Level
import java.util.logging.Logger
import java.util.{ List => JList }
import java.util.{ Map => JMap }
import scala.collection.JavaConversions._
import scala.collection.immutable.List
import scala.util.control.Exception.catching
import org.chilternquizleague.domain._
import org.chilternquizleague.domain.security._
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
import javax.servlet.ServletException
import javax.servlet.http.HttpServlet
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse
import org.chilternquizleague.util.Storage.{ entity => entityByKey }
import org.chilternquizleague.util.Storage.entityList
import org.chilternquizleague.util.Storage.save
import org.chilternquizleague.util.Storage.delete
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
import java.io.IOException
import java.io.StringWriter
import com.google.api.client.util.StringUtils
import org.apache.commons.io.IOUtils
import org.chilternquizleague.util.JacksonUtils
import org.chilternquizleague.util.UserUtils
import org.chilternquizleague.web.ViewUtils._
import java.util.HashMap
import com.fasterxml.jackson.annotation.JsonAutoDetect
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility
import java.io.Reader
import java.io.StringReader

trait BaseRest {

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

  def makeEntityList[T <: BaseEntity](entityName: String): Option[List[T]] = classFromPart(entityName) flatMap { c: Class[T] => makeEntityList(c) }
  def makeEntityList[T <: BaseEntity](c: Class[T]): Option[List[T]] = Some(entityList(c).filter(entityFilter[T]))

  def entityByParam[T](idPart: String, entityName: String): Option[T] = classFromPart[T](entityName) flatMap { clazz: Class[T] => entityByKey(idPart.toLongOpt, clazz) }

  def saveUpdate[T <: BaseEntity](reader: Reader, entityName: String): T = {

    val retval = classFromPart[T](entityName) map { clazz =>
      ofy.load.key(save(objectMapper.readValue(reader, clazz))).now
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

class EntityService extends HttpServlet with BaseRest {

  override val aliases = Map(("text", "CommonText"),
    ("global", "GlobalApplicationData")) ++ CompetitionType.values.map(t => (t.name(), t.compClass().getSimpleName))
  override def entityFilter[T] = _ => true

  override def init(config: ServletConfig) = {
    objectMapper registerModule JacksonUtils.unsafeModule
  }

  override def doGet(req: HttpServletRequest, resp: HttpServletResponse) = {
    val bits = parts(req)
    val head = bits.head
    val item = head match {

      case "global" => Application.globalData
      case "competitionType-list" => Some(CompetitionTypeView.list)
      case "dump.txt" => Option(DBDumper.dump)
      case _ => handleEntities(bits, head)

    }
    resp.setContentType("application/json")
    item.foreach(a => objectMapper.writeValue(resp.getWriter, logJson(a, "writing:")))

  }

  def rebuildStats(req: HttpServletRequest) = for (s <- entityByKey(req.id("seasonId"), classOf[Season])) yield HistoricalStatsAggregator.perform(s)

  override def doPost(req: HttpServletRequest, resp: HttpServletResponse) = {
    val bits = parts(req)
    val head = bits.head
    val item: Option[Any] = head match {
      case "rebuild-stats" => rebuildStats(req)
      case "upload-dump" =>
        DBDumper.load(objectMapper.readValue(req.getReader, classOf[JMap[String, JList[JMap[String, Any]]]])); None
      case _ => Option(saveUpdate(req.getReader, entityName(parts(req).head)))
    }

    resp.setContentType("application/json")
    item.foreach(i => objectMapper.writeValue(resp.getWriter, logJson(i, "out:")))
  }

  override def doPut(req: HttpServletRequest, resp: HttpServletResponse) = doPost(req, resp)

}

class ViewService extends HttpServlet with BaseRest {

  override val aliases = Map(("GlobalText", "CommonText"))
  override def entityFilter[T <: BaseEntity] = !_.retired

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
      case "results-for-submission" => resultsForSubmission(req)
      case "competitions-view" => competitionsForSeason(req)
      case "text" => textForName(req)
      case "reports" => resultReports(req)
      case "team-statistics" => teamStatistics(req)
      case "request-logon" => requestLogon(req.parameter("email"))
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

  def requestLogon(email: Option[String]): Option[Boolean] = {

    for {
      (u, t) <- UserUtils.userTeamForEmail(email)
    } {
      val token = LogonToken()

      for (g <- Application.globalData) {

        val idPlusPwd = f"${token.id}|${token.uuid}"

        EmailSender.apply("security", s"Your one-time password is $idPlusPwd", List(u.email))
        LOG.warning(s"one-time password is $idPlusPwd")
      }

      return Some(true)
    }

    Some(false)

  }

  def teamStatistics(req: HttpServletRequest): Option[StatisticsView] = {

    for {
      t <- entityByKey(req.id("teamId"), classOf[Team])
      s <- entityByKey(req.id("seasonId"), classOf[Season])
      stats = entityList(classOf[Statistics], ("team", t), ("season", s))
    } yield {
      stats match {
        case Nil => null
        case _ => stats.head
      }
    }
  }

  def seasons(): Option[List[SeasonView]] = makeEntityList(classOf[Season]) map { _ map { new SeasonView(_) } }

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

  def currentLeagueTable(req: HttpServletRequest, compType: CompetitionType): Option[LeagueTableWrapperView] = entityByKey(idParam(req), classOf[Season]) map (a => new LeagueTableWrapperView(a, compType))

  def teamExtras(req: HttpServletRequest): Option[TeamExtras] = {

    val teamId = idParam(req, "teamId")
    for {
      t <- entityByKey(teamId, classOf[Team])
      s <- entityByKey(idParam(req, "seasonId"), classOf[Season])
    } yield {
      new TeamExtras(t, teamFixtures(teamId, s).map(new FixturesView(_)), teamResults(teamId, s).map(new ResultsView(_)))
    }
  }

  def teamFixtures(teamId: Option[Long], season: Season, limit: Int = 20000, filter: Fixtures => Boolean = { _ => true }): List[Fixtures] = {

    val competitions = season.teamCompetitions.filter(!_.subsidiary)

    def flatMapFixtures(f: Fixtures): List[Fixtures] = {

      val newFix = Fixtures(f);
      newFix.fixtures = f.fixtures.toList filter { f => (teamId contains f.home.Id) || (teamId contains f.away.Id) }

      if (newFix.fixtures.isEmpty) Nil else List(newFix)
    }

    competitions filter { _ != null } flatMap { _.fixtures.map(_()) filter filter flatMap flatMapFixtures } sortWith (_.start before _.start) slice (0, limit)

  }

  def teamResults(teamId: Option[Long], season: Season, limit: Int = 20000, filter: Results => Boolean = { _ => true }): List[Results] = {
    val competitions = season.teamCompetitions.filter(!_.subsidiary)

    def flatMapResults(f: Results): List[Results] = {

      val newRes = Results(f);
      newRes.results = f.results filter { f => (teamId contains f.fixture.home.Id) || (teamId contains f.fixture.away.Id) }

      if (newRes.results.isEmpty) Nil else List(newRes)
    }

    competitions filter { _ != null } flatMap { _.results.map(_()) filter (_ != null) filter filter flatMap flatMapResults } sortWith (_.date before _.date) slice (0, limit)

  }

  /**
   * Relies on http params id:Season and type:CompetitionType
   */
  private def teamCompetitionForSeason(req: HttpServletRequest): Option[TeamCompetition] = {
    for {
      c <- req.parameter("type")
      t = CompetitionType.valueOf(c)
      s <- entityByKey(req.id(), classOf[Season])
    } yield s.competition(t).asInstanceOf[TeamCompetition]
  }

  def competitionResults(req: HttpServletRequest): Option[JList[ResultsView]] = teamCompetitionForSeason(req) map { _.results.map(new ResultsView(_)) }

  def competitionFixtures(req: HttpServletRequest): Option[JList[FixturesView]] = teamCompetitionForSeason(req) map { _.fixtures.map(new FixturesView(_)) }

  def textForName(req: HttpServletRequest): Option[String] = {

    Application.globalData.flatMap(g => { req.parameter("name") map { n => g.globalText.text(n) } })
  }

  def allResults(req: HttpServletRequest) =
    entityByKey(req.id(), classOf[Season]).map(_.teamCompetitions filter { !_.subsidiary } flatMap { _.results.map(new ResultsView(_)) })

  def allFixtures(req: HttpServletRequest): Option[JList[_]] =
    entityByKey(req.id(), classOf[Season]).map(_.teamCompetitions filter { !_.subsidiary } flatMap { _.fixtures.map(new FixturesView(_)) })

  def resultsForSubmission(req: HttpServletRequest): Option[PreSubmissionView] = {

    val now = new Date
    for {
      (u, t) <- UserUtils.userTeamForEmail(req.parameter("email"))
      s <- entityByKey(req.id("seasonId"), classOf[Season])
      f <- teamFixtures(Some(t.id), s).filter(_.start before now).reverse.headOption
      fixture <- f.fixtures.headOption
      comp = s.competition(f.competitionType).asInstanceOf[TeamCompetition]
      r <- comp.resultsForDate(f.start)
      p = r.findRow(fixture)
      sub = comp.subsidiaryResults(f.start)
    } yield {
      val primaryResult = p.getOrElse {
        val res = new Result
        res.fixture = fixture
        res
      }

      val report: Report = new Report
      report.team = t
      primaryResult.reports.clear
      primaryResult.reports.add(report)

      val subResult = for (r <- sub) yield r.findRow(t).getOrElse {
        val res = new Result
        res.fixture = fixture
        res
      }

      val results: List[ResultForSubmission] = List[ResultForSubmission](new ResultForSubmission(comp.`type`, primaryResult)) ++ subResult.fold(List[ResultForSubmission]())(r => List(new ResultForSubmission(comp.subsidiaryCompetition.`type`, r)))

      new PreSubmissionView(t, f, results)
    }

  }

  def competitionsForSeason(req: HttpServletRequest): Option[JList[CompetitionView]] =
    entityByKey(idParam(req), classOf[Season]).map(_.competitions.values.toList.map { a => new CompetitionView(a) })

  def resultReports(req: HttpServletRequest): Option[ResultsReportsView] = {

    for {
      t <- entityByKey(idParam(req, "homeTeamId"), classOf[Team])
      key <- req.parameter("resultsKey")
      r <- Option(ofy.load.key(Key.create(key)).now.asInstanceOf[Results])
      reps <- r.findRow(t)
    } yield {
      new ResultsReportsView(reps)
    }
  }

}

class SecureService extends EntityService {
  import javax.crypto.spec._
  import javax.crypto._
  import java.security._
  import com.google.api.client.util._
  import javax.script._
  import org.chilternquizleague.util.UserUtils._

  @JsonAutoDetect(fieldVisibility = Visibility.ANY)
  class Session(val password: String, val id: Long, val teamId: Long)

  val se = new ScriptEngineManager().getEngineByExtension("js")

  override def init(config: ServletConfig) = {

    super.init(config: ServletConfig)
    val is = this.getClass.getResourceAsStream("sjcl.js")

    val script = """
    importPackage(java.io);
    importPackage(java.lang)
    
    proxy = {}
    function loadJs(is) {
      var br = new BufferedReader(new InputStreamReader(is));
      var line = null;
      var script = "";
      while((line = br.readLine())!=null) {
          script += line;
      }

      eval(script);

      proxy = sjcl

    }    

    loadJs(is);
    
    function decrypt(password,ct){
      return proxy.decrypt(password,"" + ct)
    }

    function encrypt(password, obj){
      return proxy.encrypt(password, obj)
    }


"""
    se.put("is", is)
    se.eval(script)

  }
  
  def logTime[T](f:()=>T, message:String = "method"):T = {
      val now = System.currentTimeMillis()
      val res = f()
      LOG.warning(s"$message took ${System.currentTimeMillis - now} millis")
      res
  }

  def encrypt(value: Any, token: Token): String = {

      String.valueOf(se.asInstanceOf[Invocable].invokeFunction("encrypt", token.uuid, objectMapper.writeValueAsString(value)))

  }

  def decrypt(token: Token, payload: String) = {
    String.valueOf(se.asInstanceOf[Invocable].invokeFunction("decrypt", token.uuid, payload))
  }

  override def doGet(req: HttpServletRequest, resp: HttpServletResponse) = {
    val bits = parts(req)
    val head = bits.head
    val stripped = head.replace("-list", "")

    val sessionId = req.id("sessionId")

    val item: Option[_] = stripped match {
      case _ => handleEntities(bits, head)
    }

    for {
      sid <- sessionId
      t <- SessionToken.find(sid)
      a <- item
    } {
      objectMapper.writeValue(resp.getWriter, logJson(new Wrapper(encrypt(a, t)),"secure service"))
    }
  }

  override def doPost(req: HttpServletRequest, resp: HttpServletResponse) = {
    val bits = parts(req)
    val head = bits.head

    val session = objectMapper.readValue(req.getReader, classOf[Wrapper])
    val token = SessionToken.find(session.id)

    val item: Option[_] = head match {

      case "logon" => logon(session, req, resp)
      case _ => for (t <- decryptedText(session, token)) yield saveUpdate[BaseEntity](new StringReader(t), entityName(head))

    }

    for {
      t <- token
      a <- item
    } {
      objectMapper.writeValue(resp.getWriter, logJson(new Wrapper(encrypt(a, t)),"secure service"))
    }

  }

  def decryptedText(session: Wrapper, token: Option[SessionToken]) = for (t <- token) yield decrypt(t, session.text)

  def logon(session: Wrapper, req: HttpServletRequest, resp: HttpServletResponse) = {

    val tok = LogonToken.find(session.id)
    
    for {
      token <- tok
      (u, t) <- {

        val dec = objectMapper.readValue(decrypt(token, session.text), classOf[HashMap[String, String]])

        val email = Some(dec.get("email"))
        userTeamForEmail(email)
      }

    } {

      val sessionToken = SessionToken(u)

      objectMapper.writeValue(resp.getWriter, new Wrapper(encrypt(objectMapper.writeValueAsString(new Session(sessionToken.uuid, sessionToken.id, t.id)), token)))

      delete(token)
    }
    None
  }

}

@JsonAutoDetect(fieldVisibility = Visibility.ANY)
class Wrapper(var text: String, var id: Long = 0) {
  def this() = this(null)
}
