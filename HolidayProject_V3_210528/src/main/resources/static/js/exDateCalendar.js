





const MONTHS = ["január", "február", "március", "április", "május", "június", "július", "augusztus", "szeptember", "október", "november", "december"]
const DAYS = ["H", "K", "SZ", "CS", "P", "SZ", "V"]

const CELL_SIZE_X = "30px"
const CELL_SIZE_Y = "20px"
const TABLE_SIZE_X ="220px"
const TABLE_SIZE_Y = "160px"
const HEAD_FONT_SIZE = "14px"
const DAY_FONT_SIZE = "10px"
const CELL_FONT_SIZE = "12px"

var exEventList;
var pos;


var saved=true;

var tables = document.getElementsByTagName("table");

function clearTable() {
	
	for (let i = 0; i < tables.length; i++) {
		let actTable = tables[i];
		let rowCount = actTable.rows.length;
		for (var x=rowCount-1; x>=0; x--) {
		   actTable.deleteRow(x);
		}
		
	}
	
	calendar();
	
}


function calendar() {
	
const thisYear = new Date().getFullYear() 
const actYear = document.querySelector("input[name=btnradio]:checked").value;  // az aktualisan megtekinteni, szerkeszteni kivant ev
pos=-2;

for (let i = 0; i < tables.length; i++) {
	generateCalendar(tables[i], i);
}

for (let i= 0; i<exEventList.length;i++ ){
	
	
}

addEventsToCalendar();
clearForm('');




//function getSumLeave(actYear) {
//	let sumLeave=0;
//	for (let i in eventList) {
//		let date = new Date(eventList[i].startDate)
//		if (eventList[i].approved >= 0 && date.getFullYear()== actYear)
//				sumLeave = sumLeave + ((eventList[i].duration == 1) ? 1 : 0.5);
//	}
//	return sumLeave;
//}
//
//
//function getSumLeaveFrame(frameIndex) {
//	leaveFrame = allLeaveFrame[frameIndex];
//	return leaveFrame.baseLeave + leaveFrame.parentalLeave +leaveFrame.carriedLeave +leaveFrame.otherLeave;
//}


function generateCalendar(table, month) {
	table.style.width = TABLE_SIZE_X
	table.style.height = TABLE_SIZE_Y
	table.style.paddingLeft = "1px"
	table.style.paddingRight = "1px"
	generateTableHead(table, month);
	generateTableBody(table, month);
}




function generateTableHead(table, month) {
	let head = table.createTHead();
	let row = head.insertRow();
	let thisMonth= new Date().getMonth();
	row.style.paddingBottom = "1px";
	row.style.fontSize = HEAD_FONT_SIZE
	row.style.textAlign = "center"
	row.style.backgroundColor = "lightBlue"
	row.style.color = (month == thisMonth && actYear == thisYear) ? "brown":"black";
	let th = document.createElement("th")
	th.colSpan = "7"
	let dateText = ((month == 0)? actYear + ". " : '') + MONTHS[month];
	text = document.createTextNode(dateText)
	th.appendChild(text)
	row.appendChild(th)
	row = head.insertRow();
	row.style.paddingBottom = "1px"
	row.style.fontSize = "12px"
	row.style.textAlign = "center"
	for (let i = 0; i < 7; i++) {
		th = document.createElement("th")
		// th.style.border = "2px solid black"
		th.style.width = CELL_SIZE_X
		th.style.height = CELL_SIZE_Y
		let text = document.createTextNode(DAYS[i])
		th.appendChild(text)
		row.appendChild(th)
	}
}

function generateTableBody(table, month) {
	let body;
	if (table.tBodies.length == 0) {
		body = table.createTBody();
		} else {
			body = table.tBodies.item(0);
		}
	let row = body.insertRow()
	let text
	let dayCounter = 1
	let LengthOfMonth = daysInMonth(month)
	let firstDayOfMonth = new Date(actYear, month, 1).getDay();  // a hónap első napja milyen napra esik?
	let now = new Date()
	let today = new Date(now.getFullYear(), now.getMonth(), now.getDate())
	if (firstDayOfMonth == 0) firstDayOfMonth = 7;  // hétfővel 
	while (dayCounter <= LengthOfMonth) {
		let row = body.insertRow()
		row.style.textAlign = "center"
		for (let j = 1; j < 8; j++) {
			let cell = row.insertCell();
			if ((j >= firstDayOfMonth) && (dayCounter <= LengthOfMonth)) {
				cell.style.fontSize = CELL_FONT_SIZE
				cell.style.border = "1px solid blue"
				let day = new Date(actYear, month, dayCounter)
				if (day - today == 0) {
					cell.style.border = "2px solid red"
					cell.style.fontWeight = "bold"
					cell.style.color = "darkBlue"
				}
				cell.style.width = CELL_SIZE_X
				cell.style.height = CELL_SIZE_Y
				cell.style.userSelect = "none"
				dateStr=""+actYear +"-"+((month+1<10)? '0':'') + (month+1) +"-"+ ((dayCounter<10)? '0':'')+dayCounter;
				cell.id = dateStr;
				
				if (dayCounter < 10) text = "0"; else text = ""
				text = text + String(dayCounter++);
				cell.innerText = text
				firstDayOfMonth = 0
				if (j > 5) {
					cell.style.backgroundColor = "lightGray"
				} else {
					cell.style.backgroundColor = "white"
				}
				cell.setAttribute('onclick', 'setDateAttr(this.id)')
				cell.style.cursor = "pointer"
			} else {
				cell.innerText = "";
				cell.style.border = "0px"
			}
		}
	}
}

function daysInMonth(month) {
	return new Date(actYear, month +1,0).getDate()
}






function addEventsToCalendar() {

	for (let i in exEventList){   // kivételnapok listája
		let date = new Date(exEventList[i].date)
		let month = date.getMonth() 
		let day=date.getDate()
		
		let cellId=setDateToStr(date);
		let thisCell= document.getElementById(cellId)
		if (thisCell != null) {
		thisCell.setAttribute('title', exEventList[i].note)
		console.log(exEventList[i])
		if (exEventList[i].isWorkDay) {
			thisCell.style.backgroundColor = "GoldenRod"
			thisCell.style.color = "black"
				
			} else {
				thisCell.style.backgroundColor = "SlateGrey"
				thisCell.style.color = "lightGrey"
			}
		}
	}

	
}

}  // end of functioncalendar()




function exit() {

	if (!saved) {
		event.preventDefault();
		let modal=new bootstrap.Modal(document.getElementById("myModalExit"))
		modal.show();
	}
}



function sendrequest() {
	event.preventDefault();
	var xhr = new XMLHttpRequest();
	var rdata = exEventList;
	var data = JSON.stringify(rdata);
	xhr.onreadystatechange = function() {
		if (xhr.readyState == XMLHttpRequest.DONE) {
			if (xhr.status != 200) {
				document.body.innerText = 'Error: ' + xhr.responseText + '(' + xhr.status + ')';
			}
		}
	};
//	xhr.withCredentials = true;
	xhr.open('POST', '/saveExEventList', true);
	xhr.setRequestHeader("Content-type", "application/json");
	xhr.send(data);
	saved = true;
	document.getElementById("saveBtn").disabled = saved;
}


function clearForm(id){
	
	document.getElementById("formText").innerText ='Új kivételnap felvétele';
	document.getElementById("date").value=id;
	document.getElementById("note").value='';
	
	document.getElementById("isWorkDay").checked=false;
	document.getElementById("notWorkDay").checked=false;
	
	document.getElementById("addBtn").textContent ='Hozzáadás';
	document.getElementById("delBtn").style.display = 'none';
	
	document.getElementById("saveBtn").disabled = saved;
	
}

function setDateAttr(id) {

	
	let thisCell = document.getElementById(id)
	pos = -1;
	let date1 = new Date(id);
	date1.setHours(5)
	for (let i in exEventList) {
		let date2 = new Date(exEventList[i].date);
		date2.setHours(5)
		if (date1- date2 == 0) {
			pos = i  // már van ezen a dátumon valami az eventListben (a szabik listája)
			break;
		}
	}
	if (pos>=0) {
		
		document.getElementById("formText").textContent ='Kivételnap módositása';	
		
		document.getElementById("addBtn").textContent ='Módositás';
		document.getElementById("delBtn").style.display = 'block';
		
		document.getElementById("date").value=exEventList[pos].date;
		document.getElementById("note").value=exEventList[pos].note;
		
		document.getElementById("isWorkDay").checked=(exEventList[pos].isWorkDay);
		document.getElementById("notWorkDay").checked=!(exEventList[pos].isWorkDay);
		
		} else {
		
		clearForm(id);
		}
	
	
	document.getElementById("saveBtn").disabled = saved;
	
}


function addNewDate(){
	
	if (pos==-2) return;  // no selected date
	
	let id = document.getElementById("date").value;
	if (pos== -1 )  // new date
	{
		let date1 = new Date(id);
		let note = document.getElementById("note").value;
		let isWorkDay = document.querySelector("input[name=isWorkDay]:checked").value;
		exEventList.push({
			"id": -1,
			"date": setDateToStr(date1),
			"note":note,
			"isWorkDay":(isWorkDay == 'true')? true:false,
			});
			
			console.log(date1, isWorkDay)
	}
	
	if (pos>=0 )  // update
	{
		
		let note = document.getElementById("note").value;
		let isWorkDay = document.querySelector("input[name=isWorkDay]:checked").value;
		exEventList[pos].note = note;
		exEventList[pos].isWorkDay = (isWorkDay == 'true')? true:false;
	}
	
	saved=false;
	clearTable();
}

function markDeleteDate() {
	
	event.preventDefault();
	let modal=new bootstrap.Modal(document.getElementById("deleteModal"))
	modal.show();
	
	
}

function deleteDate() {
	
	exEventList.splice(pos,1);
	let modal=new bootstrap.Modal(document.getElementById("deleteModal"))
	modal.hide();
	saved=false;
	clearTable();
	
	
}

function setDateToStr(date){
	
	let month = date.getMonth() 
	let day=date.getDate()
	dateStr=""+date.getFullYear() +"-"+((month+1<10)? '0':'') + (month+1) +"-"+ ((day<10)? '0':'')+day;
	
	return dateStr;
}













//
//
//------------------------ dead code :) ------------------------------------
//
//
//async function getrequest(){
//	
//	var exEventList;
//	var eventList;
//	var sumLeave;
//	var leaveCounter;
//	
//	const myHeader = new Headers({
//		'Content-Type': 'application/json'		
//	});
//	
//	var myRequest = setRequest ("leaveCounter");
//	
//	
//	await fetch(myRequest)
//	.then(function(response) {
//		if(response.ok) { return response.json();
//		} else {return promise.All().reject(response);}
//	})
//	.then (function(data) {
//		leaveCounter = data;
//		
//		return fetch(setRequest("sumLeave"));
//	})
//	.then(function (response) {
//	if (response.ok) {
//		return response.json();
//	} else {
//		return Promise.reject(response);
//	}
//	})
//	.then(function (data) {
//	var sumLeave = data;
//	
//		return fetch(setRequest("eventList"));
//	})
//	.then(function (response) {
//	if (response.ok) {
//		return response.json();
//	} else {
//		return Promise.reject(response);
//	}
//	})
//	.then(function (data) {
//	var eventList = data;
//	return fetch(setRequest("eventList"));
//	})
//	.then(function (response) {
//	if (response.ok) {
//		return response.json();
//	} else {
//		return Promise.reject(response);
//	}
//	})
//	.then(function (data) {
//	var eventList = data;
//	console.log(sumLeave + ", " +  leaveCounter + eventList + ", " +  exEventList);
//	})
//	.catch(function (error) {
//	console.warn(error);
//	});
//	
//	
//	
//	
//	
//	function setRequest(selector){
//		
//	return myRequest = new Request ('http://localhost:8080/getdata',
//	{
//	method : 'POST',
//	headers: myHeader,	
//	body: selector		
//	});
//		
//	}
//	
//}
//	
//	
//	



//function getrequest(){

//	let GuestAdminUtils = {
//
//    getAllGuestsWithoutDetails : function() {
//
//        var xhr = new XMLHttpRequest();
//        xhr.open('GET', 'http://localhost:8080/getdata', true);
//
//        xhr.onload = function() {
//            if(this.status === 200) {
//                let guests = JSON.parse(this.responseText);
//                console.log(guests)
//                let output = '';
////                for(let i in guests) {
////                    output +=
////                        `<tr>
////                            <td>${guests[i].id}</td>
////                            <td>${guests[i].lastName}</td>
////                            <td>${guests[i].firstName}</td>
////                            <td>${guests[i].emailAddress}</td>
////                            <td>${guests[i].active}</td>
////                         </tr>`;
////                }
////                document.getElementById('answer').innerText = guests;
//            }
//        }
//        xhr.send();
//    }
//}
//GuestAdminUtils.getAllGuestsWithoutDetails();
//	
//	
//	
//}


