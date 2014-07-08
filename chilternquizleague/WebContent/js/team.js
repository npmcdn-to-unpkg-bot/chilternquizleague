(function() {
	mainApp.filter('afterNow', function() {
		return function(input) {
			function makeDateString(date) {
				return date.toISOString();
			}

			var now = makeDateString(new Date());
			var ret = [];
			for (idx in input) {
				if (makeDateString(input[idx].date) >= now) {
					ret.push(input[idx]);
				}
			}

			return ret;
		};
	});

	function extraStuff($scope, $interval, viewService, $location) {

		$scope.$watch("global", function(global) {

			if (global) {
				$scope.$watch("team", function(team) {

					if (team) {
						if (!team.extras) {
							viewService.view("team-extras", {
								seasonId : global.currentSeasonId,
								teamId : team.id
							}, function(extras) {

								for (idx in extras.fixtures) {

									extras.fixtures[idx].date = new Date(
											extras.fixtures[idx].date);
								}

								team.extras = extras;
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
