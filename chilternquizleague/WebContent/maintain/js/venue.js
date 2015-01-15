maintainApp.controller('VenueListCtrl', getCommonParams(makeListFn("venue", {
	sort : function(venue1, venue2) {
		return venue1.name.localeCompare(venue2.name);
	}
})));

maintainApp.controller('VenueDetailCtrl',
		getCommonParams(makeUpdateFn("venue")));

