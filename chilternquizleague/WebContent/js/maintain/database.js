maintainApp.controller('DatabaseCtrl',["$scope", "entityService", function($scope,entityService){
	
	$scope.uploadDump = function(file){
		
		var file = document.getElementById('db.dump.file').files[0];
		
		var r  = new FileReader();
		r.onload = function(e){
			
			entityService.save("upload-dump", r.result, function(){alert("Upload complete.");});
		};
		
		r.readAsText(file);
	};
	
}]);