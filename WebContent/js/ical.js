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

	
	file = fixtures.reduce(function(contents,fixtureSet){
		
		return contents + fixtureSet.fixtures.map(function(fixture){
			
			var contents = ""
			var startDate = new Date(fixture.start);
			var endDate = new Date(fixture.end);

			var description = nsg(fixture.home, "shortName") + " - " + nsg(fixture.away, "shortName")
					+ " : " + fixtureSet.description + "\n";

			contents += "BEGIN:VEVENT\n";
			contents += "DTSTAMP:" + now;
			contents += "UID:" + startDate.getTime() + "."
					+ encodeURIComponent((""+nsg(fixture.home,"shortName")).replace(/\s/g, ""))
					+ ".chilternquizleague.uk\n";
			contents += "DESCRIPTION:" + description;
			contents += "SUMMARY:" + description;

			contents += "DTSTART:" + factorAsICSDate(startDate);
			contents += "DTEND:" + factorAsICSDate(endDate);
			contents += "LOCATION:"
					+ ("" + nsg(nsgVenue(fixture.home),"name") + "," + nsg(nsgVenue(fixture.home), "address"))
							.replace(/\n\r/g, ",").replace(/\n/g, ",").replace(/\r/g, ",")
					+ "\n";
			contents += "END:VEVENT\n";
			
			return contents
			
			
		}).join()
		
	},file)

	file = file + "END:VCALENDAR\n";

	return file;

}