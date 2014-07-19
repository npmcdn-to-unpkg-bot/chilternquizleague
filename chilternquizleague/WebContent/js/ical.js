/**
 * 
 * @param fixtures - Array of fixtures
 */
function generateICalContent(fixtures){
	
	var file = "BEGIN:VCALENDAR\nVERSION:2.0\n";
	
	function factorAsICSDate(date){
		
		return date.toISOString().replace(/-/g,"").replace(/:/g,"").replace(/\.\d\d\d/,"") + "\n";
	}
	
	var now = factorAsICSDate(new Date());
	
	for(idx in fixtures){
		
		var fixture = fixtures[idx];
		var startDate = new Date(fixture.date);
		startDate.setHours(fixture.startTime.substring(0,2),fixture.startTime.substring(3),0,0,0);
		var endDate = new Date(fixture.date);
		endDate.setHours(fixture.endTime.substring(0,2),fixture.endTime.substring(3),0,0,0);
		
		var description = fixture.home.shortName + " - " + fixture.away.shortName + " : " + fixture.description + "\n";
		
		file += "BEGIN:VEVENT\n";
		file += "DTSTAMP:" + now;
		file += "UID:" + startDate.getTime() + "." + encodeURIComponent(fixture.home.shortName.replace(/\s/g,"")) + ".chilternquizleague.uk\n";
		file += "DESCRIPTION:" + description;
		file += "SUMMARY:" + description;
		
		file += "DTSTART:" + factorAsICSDate(startDate);
		file += "DTEND:" + factorAsICSDate(endDate);
		file += "LOCATION:" + ("" + fixture.home.venue.name + "," + fixture.home.venue.address).replace(/\n\r/g,",").replace(/\n/g,",").replace(/\r/g,",") + "\n";
		file += "END:VEVENT\n";
		
		
		
	}
	
	file = file + "END:VCALENDAR\n";
	
	return file;
	
}