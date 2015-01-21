/**
 * 
 * @param fixtures -
 *          Array of fixtures
 */
function generateICalContent(fixtures, venues) {

	var file = "BEGIN:VCALENDAR\nVERSION:2.0\n";

	function factorAsICSDate(date) {

		return date.toISOString().replace(/-/g, "").replace(/:/g, "").replace(
				/\.\d\d\d/, "")
				+ "\n";
	}

	/**
	 * Null-safe get 
	 */
	function nsg(object, property){
		return object && property ? object[property] : null
	}
	function nsgVenue(team){
		
		return nsg(venues, nsg(team,"venueId"))
		
	}
	
	var now = factorAsICSDate(new Date());

	for (idx in fixtures) {

		var fixtureSet = fixtures[idx];

		for (idx2 in fixtureSet.fixtures) {
			var fixture = fixtureSet.fixtures[idx2];
			var startDate = new Date(fixture.start);
			var endDate = new Date(fixture.end);

			var description = nsg(fixture.home, "shortName") + " - " + nsg(fixture.away, "shortName")
					+ " : " + fixtureSet.description + "\n";

			file += "BEGIN:VEVENT\n";
			file += "DTSTAMP:" + now;
			file += "UID:" + startDate.getTime() + "."
					+ encodeURIComponent((""+nsg(fixture.home,"shortName")).replace(/\s/g, ""))
					+ ".chilternquizleague.uk\n";
			file += "DESCRIPTION:" + description;
			file += "SUMMARY:" + description;

			file += "DTSTART:" + factorAsICSDate(startDate);
			file += "DTEND:" + factorAsICSDate(endDate);
			file += "LOCATION:"
					+ ("" + nsg(nsgVenue(fixture.home),"name") + "," + nsg(nsgVenue(fixture.home), "address"))
							.replace(/\n\r/g, ",").replace(/\n/g, ",").replace(/\r/g, ",")
					+ "\n";
			file += "END:VEVENT\n";

		}

	}

	file = file + "END:VCALENDAR\n";

	return file;

}