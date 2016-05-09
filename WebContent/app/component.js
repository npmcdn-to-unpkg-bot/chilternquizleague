
mainApp.component('app', {
  templateUrl:"/app/app.html",
  $routeConfig: [
    {path: '/', name: 'IndexContent', component: 'indexContent'},
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
