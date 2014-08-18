mainApp.controller('ContactController',['$scope', 'viewService', function($scope,viewService){

	$scope.headerText = viewService.text("find-teams-header");

}]);