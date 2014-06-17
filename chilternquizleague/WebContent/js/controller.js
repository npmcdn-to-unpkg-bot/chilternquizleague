var qlApp = angular.module('qlApp', []);

qlApp.controller('ResultsController', [ '$scope', '$http', '$interval',
		function($scope, $http, $interval) {

			$interval(function() {

				$http.get("jaxrs/leaguetable/current", {
					"responseType" : "json"
				}).success(function(ret) {
					$scope.results = ret;
				});
			}, 1000, 120);
		} ]);