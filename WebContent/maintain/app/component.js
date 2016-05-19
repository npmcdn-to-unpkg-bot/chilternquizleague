
maintainApp.component('app', {
  templateUrl:"/maintain/app/app.html",
  $routeConfig: [
    {path: '/maintain', name: 'IndexContent', component: 'indexContent', useAsDefault: true},
    {path: '/maintain/...', name: 'Root', component: 'root'},


  ]
})
.component('indexContent', {
	templateUrl:"/maintain/app/dummy.html",
})
.component('root', {
	templateUrl:"/maintain/app/root.html",
	$routeConfig:[
    {path: 'venues', name: 'Venues', component: 'venues'},
    {path: 'venues/:venueId', name: 'Venue', component: 'venue'},
    {path: 'users', name: 'Users', component: 'users'},
    {path: 'users/:userId', name: 'User', component: 'user'},
    {path: 'texts', name: 'Texts', component: 'texts'},
    {path: 'texts/:textId', name: 'Text', component: 'text'},
    {path: 'global/current', name: 'Global', component: 'global'},
    {path: 'database', name: 'Database', component: 'database'},
    {path: 'teams', name: 'Teams', component: 'teams'},
    {path: 'teams/:teamId', name: 'Team', component: 'team'},
    {path: 'mail', name: 'Mail', component: 'mail'},
    {path: 'mail/mass-mail', name: 'MassMail', component: 'massMail'},
    {path: 'stats', name: 'Stats', component: 'stats'},
    {path: 'stats/:seasonId', name: 'StatsDetail', component: 'statsDetail'},
    {path: 'seasons', name: 'Seasons', component: 'seasons'},
    {path: 'seasons/:seasonId/...', name: 'Season', component: 'season'},
	]

})
.component('venues', {
	templateUrl:"/maintain/venue/venue-list.html",
	controller : "VenueListCtrl"
})
.component('venue', {
	templateUrl:"/maintain/venue/venue-detail.html",
	controller : "VenueDetailCtrl"
})
.component('users', {
	templateUrl:"/maintain/user/user-list.html",
	controller : "UserListCtrl"
})
.component('user', {
	templateUrl:"/maintain/user/user-detail.html",
	controller : "UserDetailCtrl"
})
.component('texts', {
	templateUrl:"/maintain/text/text-list.html",
	controller : "TextListCtrl"
})
.component('text', {
	templateUrl:"/maintain/text/text-detail.html",
	controller : "TextDetailCtrl"
})
.component('global', {
	templateUrl:"/maintain/global/global-detail.html",
	controller : "GlobalDetailCtrl"
})
.component('database', {
	templateUrl:"/maintain/database/database.html",
	controller : "DatabaseCtrl"
})
.component('teams', {
	templateUrl:"/maintain/team/team-list.html",
	controller : "TeamListCtrl"
})
.component('team', {
	templateUrl:"/maintain/team/team-detail.html",
	controller : "TeamDetailCtrl"
})
.component('mail', {
	templateUrl:"/maintain/mail/mail-options.html",
})
.component('massMail', {
	templateUrl:"/maintain/mail/mass-mail.html",
	controller : "MassMailCtrl"
})
.component('stats', {
	templateUrl:"/maintain/stats/season-list.html",
	controller : "SeasonListCtrl"
})
.component('statsDetail', {
	templateUrl:"/maintain/stats/stats-detail.html",
	controller : "StatsDetailCtrl"
})
.component('seasons', {
	templateUrl:"/maintain/season/season-list.html",
	controller : "SeasonListCtrl"
})
.component('season', {
	templateUrl:"/maintain/season/season-detail.html",
	controller :"SeasonCtrl",
	$routeConfig:[
	  {path: '/', name: 'SeasonDetail', component: 'seasonDetail' ,useAsDefault:true},
	  {path: '/calendar', name: 'SeasonCalendar', component: 'seasonCalendar'},
	  {path: '/competition/:competitionId/...', name: 'Competition', component: 'competition'}
	]
})
.component('seasonDetail', {
	templateUrl:"/maintain/season/season-detail-contents.html",
	controller : "SeasonDetailCtrl",
	require : {"parent" : "^season"},
})
.component('seasonCalendar', {
	templateUrl:"/maintain/season/calendar.html",
	controller : "SeasonCalendarCtrl",
	require : {"parent" : "^season"},
})
.component("competition",	{
	templateUrl:"/maintain/competition/competition.html",
	controller : "CompetitionController",
	require : {"parent" : "^season"},
	$routeConfig:[
	  {path: 'LEAGUE', name: 'LeagueDetail', component: 'leagueDetail'},
	  {path: 'LEAGUE/results', name: 'LeagueResults', component: 'competitionResults'},
	  {path: 'LEAGUE/fixtures', name: 'LeagueFixtures', component: 'competitionFixtures'},
	  {path: 'LEAGUE/tables', name: 'LeagueTables', component: 'competitionTables'},
	  {path: 'BEER', name: 'BeerDetail', component: 'beerDetail'},
	  {path: 'BEER/results', name: 'BeerResults', component: 'competitionResults'},
	  {path: 'BEER/tables', name: 'BeerTables', component: 'competitionTables'},
	  {path: 'CUP', name: 'CupDetail', component: 'cupDetail'},
	  {path: 'CUP/results', name: 'CupResults', component: 'competitionResults'},
	  {path: 'CUP/fixtures', name: 'CupFixtures', component: 'competitionFixtures'},
	  {path: 'PLATE', name: 'PlateDetail', component: 'plateDetail'},
	  {path: 'PLATE/results', name: 'PlateResults', component: 'competitionResults'},
	  {path: 'PLATE/fixtures', name: 'PlateFixtures', component: 'competitionFixtures'},
	  {path: 'BUZZER', name: 'BuzzerDetail', component: 'buzzerDetail'},
	  {path: 'INDIVIDUAL', name: 'IndividualDetail', component: 'individualDetail'},
	]
	})
	.component("leagueDetail",{
		templateUrl:"/maintain/competition/league-detail.html",
		controller: "CompetitionDetailController",
		require : {"parent" : "^competition"}
	})
	.component("beerDetail",{
		templateUrl:"/maintain/competition/beer-detail.html",
		controller: "CompetitionDetailController",
		require : {"parent" : "^competition"}
	})
	.component("cupDetail",{
		templateUrl:"/maintain/competition/cup-detail.html",
		controller: "CompetitionDetailController",
		require : {"parent" : "^competition"}
	})
	.component("plateDetail",{
		templateUrl:"/maintain/competition/plate-detail.html",
		controller: "CompetitionDetailController",
		require : {"parent" : "^competition"}
	})
	.directive("knockoutDetail",function() {
	  return {templateUrl:"/maintain/competition/knockout-detail.html",}
	})
	.component("buzzerDetail",{
		templateUrl:"/maintain/competition/buzzer-detail.html",
		controller: "CompetitionDetailController",
		require : {"parent" : "^competition"}
	})
	.component("individualDetail",{
		templateUrl:"/maintain/competition/individual-detail.html",
		controller: "CompetitionDetailController",
		require : {"parent" : "^competition"}
	})
	.component("competitionResults",{
		templateUrl:"/maintain/competition/results.html",
		controller: "ResultsCtrl",
		require : {"parent" : "^competition"}
	})
	.component("competitionFixtures",{
		templateUrl:"/maintain/competition/fixtures.html",
		controller: "FixturesCtrl",
		require : {"parent" : "^competition"}
	})
	.component("competitionTables",{
		templateUrl:"/maintain/competition/tables.html",
		controller: "LeagueTablesCtrl",
		require : {"parent" : "^competition"}
	})


/*

	$stateProvider.state("home", {
	    url: "/maintain",
	    templateUrl: '/maintain/dummy.html'
	})
	.state("venues", {
		url : "/maintain/venues",
		templateUrl : "/maintain/venue/venue-list.html"
	})
	.state("venue", {
		url : "/maintain/venues/:venueId",
		templateUrl : '/maintain/venue/venue-detail.html'
	})
	.state("teams", {
		url : "/maintain/teams",
		templateUrl : "/maintain/team/team-list.html"
	})
	.state("team", {
		url : "/maintain/teams/:teamId",
		templateUrl : '/maintain/team/team-detail.html'
	})

	.state('users', {
		templateUrl : '/maintain/user/user-list.html',
		url : '/maintain/users'})
	.state('user', {
		templateUrl : '/maintain/user/user-detail.html',
		url : '/maintain/users/:userId'})
	.state('seasons', {
		templateUrl : '/maintain/season/season-list.html',
		url : '/maintain/seasons'})
	.state("season", {
		templateUrl : '/maintain/season/season-detail.html'})
	.state("season.detail", {
		templateUrl:"/maintain/season/season-detail-contents.html",
		url : '/maintain/seasons/:seasonId'}
	)
	.state('season.calendar', {
		templateUrl : '/maintain/season/calendar.html',
		url: "/maintain/seasons/:seasonId/calendar"}
	)
	.state('season.competition', {
		templateUrl : '/maintain/competition/competition-container.html',
		url: "/maintain/seasons/:seasonId/competition"}
	)
	.state("season.competition.detail", {
		templateUrl : function(params){return "/maintain/competition/" + params.compType.toLowerCase() + "-detail.html"},
		url : "/:compType"}
	)

	.state("season.competition.fixtures", {
		templateUrl : '/maintain/competition/fixtures.html',
		url : '/:compType/fixtures'})
	.state("season.competition.results", {
		templateUrl : '/maintain/competition/results.html',
		url : '/:compType/results'})
	.state("season.competition.tables", {
		templateUrl : '/maintain/competition/tables.html',
		url : '/:compType/tables'})
	.state("global", {
		templateUrl : '/maintain/global/global-detail.html',
		url : '/maintain/global/current'})
	.state('texts', {
		templateUrl : '/maintain/text/text-list.html',
		url : '/maintain/texts'})
	.state('text', {
		templateUrl : '/maintain/text/text-detail.html',
		url : '/maintain/texts/:textId'})
	.state('stats', {
		templateUrl : '/maintain/stats/season-list.html',
		url : '/maintain/stats'})
	.state('stats-detail', {
		templateUrl : '/maintain/stats/stats-detail.html',
		url: "/maintain/stats/:seasonId"})

	.state("database", {
		templateUrl : '/maintain/database/database.html',
		url:'/maintain/database'})
	.state('mail', {
		templateUrl : '/maintain/mail/mail-options.html',
		url : '/maintain/mail'})
	.state('mass-mail', {
		templateUrl : '/maintain/mail/mass-mail.html',
		url : '/maintain/mail/mass-mail'})
*/