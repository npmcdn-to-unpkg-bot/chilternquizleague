var qlApp = angular.module('qlApp', []).factory('viewService',
		VIEW_SERVICE_DEFN);

qlApp.controller('ResultsController', [ '$scope', '$interval',
		'viewService', function($scope, $interval, viewService) {

			viewService.view("globaldata", {}, function(globalData) {

				function loadTable() {
					viewService.view("leaguetable", {
						id : globalData.currentSeasonId
					}, function(ret) {
						$scope.season = ret;
					});
				}

				$scope.leagueName = globalData.leagueName;
				$scope.frontPageText = globalData.frontPageText;

				loadTable();
				$interval(loadTable, 1000, 120)["catch"](function() {
					$interval.cancel(this);
				});
			});

		} ]);
