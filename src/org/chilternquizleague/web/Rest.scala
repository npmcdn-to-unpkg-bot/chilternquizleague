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
import org.chilternquizleague.util.Storage._
import org.chilternquizleague.util.ClassUtils._
import org.chilternquizleague.util.LogUtils._
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
import org.chilternquizleague.util.JacksonUtils.ObjectMapperImprovements
import org.chilternquizleague.util.UserUtils
import org.chilternquizleague.web.ViewUtils._
import java.util.HashMap
import com.fasterxml.jackson.annotation.JsonAutoDetect
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility
import java.io.Reader
import java.io.StringReader
import javax.servlet.http.Cookie
import java.net.URL
import com.google.appengine.api.taskqueue.TaskOptions.Builder._
import com.google.appengine.api.taskqueue.QueueFactory
import javax.mail.Message.RecipientType
import org.chilternquizleague.util.LocalExample
import org.chilternquizleague.util.CloudStorage
import java.io.InputStream
import java.text.SimpleDateFormat

trait BaseRest {

  import scala.reflect._
  
  val LOG: Logger = Logger.getLogger(classOf[BaseRest].getName());
  val cookieName = "qlsessionid"

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
      case _ => for {
        idPart <- parts.tail.headOption
        e <- entityByParam[BaseEntity](idPart, entName)
      } yield e
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

  def sessionCookieId(id:Long, resp:HttpServletResponse, duration:Int) = {
    
    val cookie = new Cookie(cookieName,String.valueOf(id))
    cookie.setPath("/secure/")
    cookie.setMaxAge(duration)
    
    resp.addCookie(cookie)
   
  }
  
  def readObject[T:ClassTag](req:HttpServletRequest):T = objectMapper.read[T](req.getReader).asInstanceOf[T] 

  
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

      case "leagueTable" => Some(new LeagueTable)
      case "leagueTableRow" => Some(new LeagueTableRow)
      case "global" => Application.globalData
      case "competitionType-list" => Some(CompetitionTypeView.list)
      case "dump.txt" => Option(DBDumper.dump)
      case "file-acl" => for(fileName <- bits.tail.headOption) yield CloudStorage.getACL(fileName)
      case _ => handleEntities(bits, head)

    }
    resp.setContentType("application/json")
    item.foreach(a => objectMapper.writeValue(resp.getWriter, logJson(a, "writing:")))

  }
  
  def rebuildStats(req: HttpServletRequest) = for (s <- entityByKey[Season](req.id("seasonId"))) yield HistoricalStatsAggregator.perform(s)
  def recalculateTables(req: HttpServletRequest) = for(c <- entityByKey[BaseLeagueCompetition](req.id("competitionId"))) yield {c.recalculateTables}
  
  def massMail(request:MassMailRequest, host:String):Option[String]={
    
    val addresses = for{
      t <- entities[Team]()
      u <- t.users
    }yield u.email
    
    EmailSender(s"${request.sender}@$host", request.text, addresses, recipientType = RecipientType.BCC,subject=request.subject)
    
    Some("")
  }
  
  def upload(fileName:Option[String], mimeType:String, reader:InputStream) = {
    for(n <- fileName) yield CloudStorage.saveFile(n, mimeType, reader)
     
  }
  
  override def doPost(req: HttpServletRequest, resp: HttpServletResponse) = {
    val bits = parts(req)
    val head = bits.head
    val mime = req.getContentType
    val item: Option[Any] = head match {
      case "rebuild-stats" => rebuildStats(req)
      case "upload-dump" => DBDumper.load(readObject[JMap[String,JList[JMap[String,Any]]]](req)); None
      case "mass-mail" => massMail(readObject[MassMailRequest](req),req.host)  
      case "upload" => upload(req.parameter("name") , req.getContentType, req.getInputStream)
      case "recalculateTables" => recalculateTables(req)
      case _ => Option(saveUpdate(req.getReader, entityName(bits.head)))
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
      case "request-logon" => requestLogon(req.parameter("email"), req,resp)
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

  def requestLogon(email: Option[String], req:HttpServletRequest, resp:HttpServletResponse): Option[RequestLogonResult] = {

    for {
      (u, t) <- UserUtils.userTeamForEmail(email)
    } {
      val token = LogonToken()

      for (g <- Application.globalData) {

        val pwd = token.uuid
        sessionCookieId(token.id, resp, LogonToken.duration/1000)
      
        val host = new URL(req.getRequestURL.toString()).getHost
        
        EmailSender.apply(s"security@$host", s"Your one-time password is $pwd\nPlease paste this into the 'Password' field in your browser.\n\nThis password will expire in 15 minutes.", List(u.email))
        Logger.getLogger(this.getClass.getName + ".requestLogon").fine(s"one-time password is $pwd")
      }
      
      return Some(new RequestLogonResult(true))
    }

    Some(new RequestLogonResult(false))

  }

  def teamStatistics(req: HttpServletRequest): Option[StatisticsView] = {

    for {
      t <- entityByKey[Team](req.id("teamId"))
      s <- entityByKey[Season](req.id("seasonId"))
      stats = entities[Statistics]().filter(st =>st.team.id == t.id && st.season.id == s.id)
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

  def currentLeagueTable(req: HttpServletRequest, compType: CompetitionType): Option[LeagueTableWrapperView] = entityByKey[Season](idParam(req)) map (a => new LeagueTableWrapperView(a, compType))

  def teamExtras(req: HttpServletRequest): Option[TeamExtras] = {

    val teamId = idParam(req, "teamId")
    for {
      t <- entityByKey[Team](teamId)
      s <- entityByKey[Season](idParam(req, "seasonId"))
    } yield {
      new TeamExtras(t, teamFixtures(teamId, s).map(new FixturesView(_)), teamResults(teamId, s).map(new ResultsView(_)), s.positions(t))
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
      s <- entityByKey[Season](req.id())
    } yield s.competition(t).asInstanceOf[TeamCompetition]
  }

  def competitionResults(req: HttpServletRequest): Option[JList[ResultsView]] = teamCompetitionForSeason(req) map { _.results.map(new ResultsView(_)) }

  def competitionFixtures(req: HttpServletRequest): Option[JList[FixturesView]] = teamCompetitionForSeason(req) map { _.fixtures.map(new FixturesView(_)) }

  def textForName(req: HttpServletRequest): Option[String] = {

    Application.globalData.flatMap(g => { req.parameter("name") map { n => g.globalText.text(n) } })
  }

  def allResults(req: HttpServletRequest) =
    entityByKey[Season](req.id()).map(_.teamCompetitions filter { !_.subsidiary } flatMap { _.results.map(new ResultsView(_)) })

  def allFixtures(req: HttpServletRequest): Option[JList[_]] =
    entityByKey[Season](req.id()).map(_.teamCompetitions filter { !_.subsidiary } flatMap { _.fixtures.map(new FixturesView(_)) })

  def resultsForSubmission(req: HttpServletRequest): Option[PreSubmissionView] = {

    val now = new Date
    for {
      (u, t) <- UserUtils.userTeamForEmail(req.parameter("email"))
      s <- entityByKey[Season](req.id("seasonId"))
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
    entityByKey[Season](idParam(req)).map(_.competitions.values.toList.map { a => new CompetitionView(a) })

  def resultReports(req: HttpServletRequest): Option[ResultsReportsView] = {

    for {
      t <- entityByKey[Team](idParam(req, "homeTeamId"))
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
  import org.chilternquizleague.util.Crypto

  @JsonAutoDetect(fieldVisibility = Visibility.ANY)
  class Session(val password: String, val id: Long, val teamId: Long)



  def encrypt(value: Any, token: Token): String = {

   val json = objectMapper.writeValueAsString(value)
    
    logTime(()=>Crypto.encrypt(json, token.uuid), "encrypt")
  
  }

  def decrypt(token: Token, payload: String):String = {
    
    logTime(()=>Crypto.decrypt(payload, token.uuid), "decrypt")

  }

  def sessionId(req: HttpServletRequest) = {

    if (req.getCookies == null) Some(0L) else
      for {
        cookie <- req.getCookies.find { _.getName == cookieName }
        sessionId <- cookie.getValue.toLongOpt
      } yield sessionId

  }

  override def doGet(req: HttpServletRequest, resp: HttpServletResponse) = {
    val bits = parts(req)
    val head = bits.head
    val stripped = head.replace("-list", "")

    val item: Option[_] = stripped match {
      case _ => handleEntities(bits, head)
    }

    for {
      sessionId <- sessionId(req)
      t <- SessionToken.find(sessionId)
      a <- item
    } {
      objectMapper.writeValue(resp.getWriter, logJson(new Wrapper(encrypt(a, t)), "secure service"))
    }
  }

  override def doPost(req: HttpServletRequest, resp: HttpServletResponse) = {
    val bits = parts(req)
    val head = bits.head

    val payload = objectMapper.readValue(req.getReader, classOf[Wrapper])

    def doSave() = {
      for {
        sessionId <- sessionId(req)
        t <- SessionToken.find(sessionId)
        a = decrypt(t, payload.text)
        i = saveUpdate[BaseEntity](new StringReader(a), entityName(head))
      } yield new Wrapper(encrypt(i, t))
    }

    val item: Option[_] =

      head match {
        case "logon" => logon(payload, req, resp)
        case _ => doSave()
      }

    for (i <- item) {
      objectMapper.writeValue(resp.getWriter, logJson(i, "secure service"))
    }
  }

  def logon(session: Wrapper, req: HttpServletRequest, resp: HttpServletResponse) = {

    for {
      sessionId <- sessionId(req)
      token <- LogonToken.find(sessionId)
      (u, t) <- {

        val dec = objectMapper.readValue(decrypt(token, session.text), classOf[HashMap[String, String]])

        val email = Some(dec.get("email"))
        userTeamForEmail(email)
      }

    } {

      val sessionToken = SessionToken(u)

      sessionCookieId(sessionToken.id, resp, SessionToken.duration/1000)
      objectMapper.writeValue(resp.getWriter, new Wrapper(encrypt(objectMapper.writeValueAsString(new Session(sessionToken.uuid, sessionToken.id, t.id)), token)))
      val queue = QueueFactory.getQueue("tokens");
      queue.add(withUrl("/tasks/tokens").countdownMillis(SessionToken.duration * 2));

      delete(token)
    }
    None
  }

}

class CalendarService extends ViewService{
  val dateFormatString = "yyyyMMdd'T'HHmmss'Z'"
  override def doPost(req: HttpServletRequest, resp: HttpServletResponse) = {}  
  override def doGet(req: HttpServletRequest, resp: HttpServletResponse) = {
      val bits = parts(req)
      val head = bits.head
      val id = bits.tail.head
      
      val contents = head match{
        
        case "team" => makeICal(entity[Team](Option(id.toLong)))
        case _ => ""
        
      }
      
      resp.setContentType("text/calendar")
      resp.getWriter.append(contents)
      resp.getWriter.flush()
    }
    
    private def formatEvent(event:Event, text:String):String = {
      
      val dateFormat = new SimpleDateFormat(dateFormatString)
      val now = dateFormat.format(new Date())
      val uidPart = text.replaceAll("\\s", "") 
      val address = event.venue.address.replaceAll("\\n\\r", ",").replaceAll("\\n", ",").replaceAll("\\r", ",")
      s"""
BEGIN:VEVENT
DTSTAMP:$now
UID:${event.start.getTime}.$uidPart.chilternquizleague.uk
DESCRIPTION:$text
SUMMARY:$text
DTSTART:${dateFormat.format(event.start)}
DTEND:${dateFormat.format(event.end)}
LOCATION:${event.venue.name},$address
END:VEVENT
"""

    }
    private def formatFixture(fixture:Fixture, description:String) = {
      
      val text = s"${fixture.home.shortName} - ${fixture.away.shortName} : $description"
      val dateFormat = new SimpleDateFormat(dateFormatString)
      val now = dateFormat.format(new Date())
      val uidPart = fixture.home.shortName.replaceAll("\\s", "") 
      val address = fixture.home.venue.address.replaceAll("\\n\\r", ",").replaceAll("\\n", ",").replaceAll("\\r", ",")
      s"""
BEGIN:VEVENT
DTSTAMP:$now
UID:${fixture.start.getTime}.$uidPart.chilternquizleague.uk
DESCRIPTION:$text
SUMMARY:$text
DTSTART:${dateFormat.format(fixture.start)}
DTEND:${dateFormat.format(fixture.end)}
LOCATION:${fixture.home.venue.name},$address
END:VEVENT
"""

    }
    private def formatBlankFixtures(fixtures:Fixtures) = {
      
      val dateFormat = new SimpleDateFormat(dateFormatString)
      val now = dateFormat.format(new Date())
      val uidPart = fixtures.description.replaceAll("\\s", "") 
      s"""
BEGIN:VEVENT
DTSTAMP:$now
UID:${fixtures.start.getTime}.$uidPart.chilternquizleague.uk
DESCRIPTION:${fixtures.description}
SUMMARY:${fixtures.description}
DTSTART:${dateFormat.format(fixtures.start)}
DTEND:${dateFormat.format(fixtures.end)}
END:VEVENT
"""

    }

    private def makeICal(team:Option[Team]):String = {
      val builder = new StringBuilder("BEGIN:VCALENDAR\nVERSION:2.0\n")
      
      for{
        t <- team
        gap <- Application.globalData
      }
      yield{
        for{
          c <- gap.currentSeason.teamCompetitions
          fixtures <- c.fixtures
          f <- fixtures.findRow(t)
        }
        yield{
          builder.append(formatFixture(f, fixtures.description))
        }
        for{
          c <- gap.currentSeason.singletonCompetitions
        
        }
        yield{
          builder.append(formatEvent(c.event, s"${gap.leagueName} ${c.description}"))
        }
        for{
          c <- gap.currentSeason.teamCompetitions
          fixtures <- c.fixtures if fixtures.fixtures.isEmpty()
                 }
        yield{
          builder.append(formatBlankFixtures(fixtures))
        }

      }
      builder.append("END:VCALENDAR\n").toString()
    }
  
}

@JsonAutoDetect(fieldVisibility = Visibility.ANY)
class Wrapper(var text: String, var id: Long = 0) {
  def this() = this(null)
}
