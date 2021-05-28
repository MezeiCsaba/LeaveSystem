/**
 * 
 */


var check = function() {

	
	if ((document.getElementById('password').value == document
			.getElementById('confirmPassword').value)) {
		document.getElementById('confirmPassword').setAttribute(
				"style", "background-color:#ecf9ec");
		document.getElementById('saveForm').disabled = false;
	} else {
		document.getElementById('confirmPassword').setAttribute(
				"style", "background-color:#ffe6e6");
		document.getElementById('saveForm').disabled = true;
	}
	
}	
	
//	
//	ízlés dolga ...  ;) 
//	
//	if ((document.getElementById('password').value.length > 0 && 
//		document.getElementById('confirmPassword').value.length > 0) && 
//		(document.getElementById('password').value != document
//			.getElementById('confirmPassword').value)) {
//			document.getElementById('confirmPassword').setAttribute(
//				"style", "background-color:#ffe6e6");
//		document.getElementById('saveForm').disabled = true;
//		} else {
//		document.getElementById('confirmPassword').setAttribute(
//				"style", "background-color:#ecf9ec");
//		document.getElementById('saveForm').disabled = false;
//}


var checkModal = function() {
	
	if ((document.getElementById('mpassword').value == document
			.getElementById('mconfirmPassword').value)) {
		document.getElementById('mconfirmPassword').setAttribute(
				"style", "background-color:#ecf9ec");
		if (document.getElementById('mconfirmPassword').value.length > 0) {
		document.getElementById('saveButton').disabled = false;}
	} else {
		document.getElementById('mconfirmPassword').setAttribute(
				"style", "background-color:#ffe6e6");
		document.getElementById('saveButton').disabled = true;
	}
	


}



