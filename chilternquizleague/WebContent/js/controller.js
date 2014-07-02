var qlApp = angular.module('qlApp', []).factory('viewService',
		VIEW_SERVICE_DEFN);

qlApp.controller('ResultsController', [
		'$scope',
		'$http',
		'$interval','viewService',
		function($scope, $http, $interval, viewService) {

			viewService.view("globaldata", function(globalData) {

				$scope.leagueName = globalData.leagueName;
				$scope.frontPageText = globalData.frontPageText;

				var promise = $interval(function() {

					viewService.view("leaguetable", {id: globalData.currentSeasonId},function(ret) {
						$scope.season = ret;
					}).error(function() {
						$interval.cancel(promise)
					} );
				
				}, 1000, 120);
			});
			

		} ]);

