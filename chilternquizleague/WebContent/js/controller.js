var mainApp = angular.module('mainApp', []).factory('viewService',
		VIEW_SERVICE_DEFN);

mainApp.controller('MainController', [ '$scope', '$interval', 'viewService',
		function($scope, $interval, viewService) {

			viewService.view("globaldata", {}, function(globalData) {
				$scope.global = globalData;
				
			});

		} ]);
