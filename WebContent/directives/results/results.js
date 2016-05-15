mainApp.directive('cqlResults',["$mdDialog", function($mdDialog) {
    return {
    	scope:{results:"=",
    		type:"="},
    	restrict:'E',
    	templateUrl:'/directives/results/results-table-content.html',
    	link : function(scope, element, attrs){
    		
    		scope.close = function(){$mdDialog.hide()}
    		scope.rowCount = attrs.rows ? attrs.rows :10000;
    		scope.cardStyle = attrs.hasOwnProperty("noCard") ? {'box-shadow':'unset'} : {};
    		scope.showInfo = function($event,content){
  				
  				$mdDialog.show(
  			      $mdDialog.alert()
  			        .textContent(content)
  			        .ok('Close')
  			        .targetEvent($event))
  			}
    		scope.showReports = function(ev,results, result) {

  				$mdDialog.show({
  					scope : scope,
  					preserveScope: true,
  					templateUrl : '/directives/results/reports.html',
  					controller : "ReportsDialogController",
  					targetEvent : ev,
  					clickOutsideToClose:true,
  					locals : {
  						reportsData : {
  							results : results,
  							result : result
  						}
  					}
  				});

  			};
    		
    	}
    	
    };
  }])
.controller("ReportsDialogController", [ '$scope','viewService','reportsData',
	function($scope,viewService,reportsData) {

		$scope.reportsData = reportsData;	
		
		if (reportsData) {
				$scope.reports = viewService.view("reports", {
					resultsKey : reportsData.results.key,
					homeTeamId : reportsData.result.fixture.home.id

			});}

	} ]);;
