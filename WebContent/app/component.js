
mainApp.component('app', {
  templateUrl:"/app/app.html",
  $routeConfig: [
    {path: '/', name: 'IndexContent', component: 'indexContent', useAsDefault: true},
    {path: '/rules', name: 'Rules', component: 'rules'},
    {path: '/contact', name: 'Contact', component: 'contact'},
    {path: '/links', name: 'Links', component: 'links'},
    {path: '/calendar_view', name: 'Calendar', component: 'calendar'},
    {path: '/results/...', name: 'Results', component: 'results'},
    {path: '/fixtures/...', name: 'Fixtures', component: 'fixtures'},
    {path: '/reports/...', name: 'Reports', component: 'reports'},
    {path: '/results/submit', name: 'SubmitResults', component: 'submitResults'},
    {path: '/competitions/...', name: 'Competitions', component: 'competitions'},
    {path: '/venues/...', name: 'Venues', component: 'venues'},
  ]
})
.component('indexContent', {
	templateUrl:"/app/indexContents.html",
	controller : "IndexContentsController"
})
.controller("IndexContentsController", ["$scope","seasonService", function($scope, seasonService){
	seasonService.getGlobal().then(function(global){
		$scope.global = global;
	});
}])
