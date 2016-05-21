
maintainApp.component('app', {
  templateUrl:"components/app/app.html",
  $routeConfig: [
    {path: '/', name: 'IndexContent', component: 'indexContent', useAsDefault: true},
    {path: '/...', name: 'Root', component: 'root'},


  ]
})
.component('indexContent', {
	templateUrl:"components/app/dummy.html",
})
.component('root', {
	templateUrl:"components/app/root.html",
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
	templateUrl:"components/venue/venue-list.html",
	controller : "VenueListCtrl"
})
.component('venue', {
	templateUrl:"components/venue/venue-detail.html",
	controller : "VenueDetailCtrl"
})
.component('users', {
	templateUrl:"components/user/user-list.html",
	controller : "UserListCtrl"
})
.component('user', {
	templateUrl:"components/user/user-detail.html",
	controller : "UserDetailCtrl"
})
.component('texts', {
	templateUrl:"components/text/text-list.html",
	controller : "TextListCtrl"
})
.component('text', {
	templateUrl:"components/text/text-detail.html",
	controller : "TextDetailCtrl"
})
.component('global', {
	templateUrl:"components/global/global-detail.html",
	controller : "GlobalDetailCtrl"
})
.component('database', {
	templateUrl:"components/database/database.html",
	controller : "DatabaseCtrl"
})
.component('teams', {
	templateUrl:"components/team/team-list.html",
	controller : "TeamListCtrl"
})
.component('team', {
	templateUrl:"components/team/team-detail.html",
	controller : "TeamDetailCtrl"
})
.component('mail', {
	templateUrl:"components/mail/mail-options.html",
})
.component('massMail', {
	templateUrl:"components/mail/mass-mail.html",
	controller : "MassMailCtrl"
})
.component('stats', {
	templateUrl:"components/stats/season-list.html",
	controller : "SeasonListCtrl"
})
.component('statsDetail', {
	templateUrl:"components/stats/stats-detail.html",
	controller : "StatsDetailCtrl"
})
.component('seasons', {
	templateUrl:"components/season/season-list.html",
	controller : "SeasonListCtrl"
})
.component('season', {
	templateUrl:"components/season/season-detail.html",
	controller :"SeasonCtrl",
	$routeConfig:[
	  {path: '/', name: 'SeasonDetail', component: 'seasonDetail' ,useAsDefault:true},
	  {path: '/calendar', name: 'SeasonCalendar', component: 'seasonCalendar'},
	  {path: '/competition/:competitionId/...', name: 'Competition', component: 'competition'}
	]
})
.component('seasonDetail', {
	templateUrl:"components/season/season-detail-contents.html",
	controller : "SeasonDetailCtrl",
	require : {"parent" : "^season"},
})
.component('seasonCalendar', {
	templateUrl:"components/season/calendar.html",
	controller : "SeasonCalendarCtrl",
	require : {"parent" : "^season"},
})
.component("competition",	{
	templateUrl:"components/competition/competition.html",
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
		templateUrl:"components/competition/league-detail.html",
		controller: "CompetitionDetailController",
		require : {"parent" : "^competition"}
	})
	.component("beerDetail",{
		templateUrl:"components/competition/beer-detail.html",
		controller: "CompetitionDetailController",
		require : {"parent" : "^competition"}
	})
	.component("cupDetail",{
		templateUrl:"components/competition/cup-detail.html",
		controller: "CompetitionDetailController",
		require : {"parent" : "^competition"}
	})
	.component("plateDetail",{
		templateUrl:"components/competition/plate-detail.html",
		controller: "CompetitionDetailController",
		require : {"parent" : "^competition"}
	})
	.directive("knockoutDetail",function() {
	  return {templateUrl:"components/competition/knockout-detail.html",}
	})
	.component("buzzerDetail",{
		templateUrl:"components/competition/buzzer-detail.html",
		controller: "CompetitionDetailController",
		require : {"parent" : "^competition"}
	})
	.component("individualDetail",{
		templateUrl:"components/competition/individual-detail.html",
		controller: "CompetitionDetailController",
		require : {"parent" : "^competition"}
	})
	.component("competitionResults",{
		templateUrl:"components/competition/results.html",
		controller: "ResultsCtrl",
		require : {"parent" : "^competition"}
	})
	.component("competitionFixtures",{
		templateUrl:"components/competition/fixtures.html",
		controller: "FixturesCtrl",
		require : {"parent" : "^competition"}
	})
	.component("competitionTables",{
		templateUrl:"components/competition/tables.html",
		controller: "LeagueTablesCtrl",
		require : {"parent" : "^competition"}
	})
