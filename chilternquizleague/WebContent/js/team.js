(function() {
	mainApp.filter('afterNow', function() {
		return function(input) {
			function makeDateString(date) {
				return new Date(date).toISOString();
			}

			var now = makeDateString(new Date());
			var ret = [];
			for (idx in input) {
				if (makeDateString(input[idx].start) >= now) {
					ret.push(input[idx]);
				}
			}

			return ret;
		};
	});

	function extraStuff($scope, $interval, viewService, $location) {

		$scope.makeICal = function(team) {

			var contents = generateICalContent(team.extras.fixtures);

			var filename = team.shortName.replace(/\s/g, "_") + "_fixtures"
					+ ".ics";

			var blob = new Blob([ contents ], {
				type : "text/calendar;charset=utf-8"
			});

			saveAs(blob, filename);

		};
	
		$scope.$watch("global.currentSeasonId", function(currentSeasonId) {

			if (currentSeasonId) {
				$scope.$watch("team", function(team) {

					if (team) {
						if (!team.extras) {
							team.extras = viewService.view("team-extras", {
								seasonId : currentSeasonId,
								teamId : team.id
							});
						}
					}
				});

			}
		});
	}

	mainApp.controller('TeamsController', [ '$scope', '$interval', 'viewService',
			'$location', cyclingListControllerFactory("team", function(team1, team2) {
				return team1.shortName.localeCompare(team2.shortName);
			}, extraStuff) ]);

})();
