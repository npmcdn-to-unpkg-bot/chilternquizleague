/**
 * 
 * @param fixtures - Array of fixtures
 */
function generateICalContent(fixtures){
	
	var file = "BEGIN:VCALENDAR\n";
	
	function factorAsICSDate(date){
		
		return date.toISOString().replace(/-/g,"").replace(/:/g,"").replace(/\.\d\d\d/,"") + "\n";
	}
	
	var now = factorAsICSDate(new Date());
	
	for(idx in fixtures){
		
		var fixture = fixtures[idx];
		var startDate = new Date(fixture.date);
		startDate.setHours(20,30,0,0,0);
		var endDate = new Date(fixture.date);
		endDate.setHours(22,0,0,0);
		
		file += "BEGIN:VEVENT\n";
		file += "DTSTAMP:" + now;
		file += "UID:" + startDate.getTime() + "." + fixture.home.shortName.replace(/\s/g,"") + ".chilternquizleague.uk\n";
		file += "DESCRIPTION:" + fixture.home.shortName + " - " + fixture.away.shortName + " : " + fixture.competition + "\n";
		file += "SUMMARY:" + fixture.home.shortName + " - " + fixture.away.shortName + " : " + fixture.competition + "\n";
		
		file += "DTSTART:" + factorAsICSDate(startDate);
		file += "DTEND:" + factorAsICSDate(endDate);
		file += "LOCATION:" + ("" + fixture.home.venue.name + "," + fixture.home.venue.address).replace(/\n\r/g,",").replace(/\n/g,",").replace(/\r/g,",") + "\n";
		file += "END:VEVENT\n";
		
		
		
	}
	
	file = file + "END:VCALENDAR\n";
	
	return file;
	
}