





const MONTHS = ["január", "február", "március", "április", "május", "június", "július", "augusztus", "szeptember", "október", "november", "december"]
const DAYS = ["H", "K", "SZ", "CS", "P", "SZ", "V"]

const CELL_SIZE_X = "28px"
const CELL_SIZE_Y = "25px"
const TABLE_SIZE_X = "220px"
const TABLE_SIZE_Y = "190px"
const HEAD_FONT_SIZE = "16px"
const CELL_FONT_SIZE = "14px"

var exEventList;
var eventList;
var allLeaveFrame;
var isFreeLeave;
var sumLeave;
var leaveCounter;

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
var frameIndex = actYear - thisYear + (allLeaveFrame.length ==3)? 1:0;   // last year 0 , next year 1, actual year 0

sumLeave =getSumLeaveFrame(frameIndex);
leaveCounter = getSumLeave(actYear);

isFreeLeave = (sumLeave - leaveCounter) > 0;


document.getElementById("saveBtn").disabled = saved;

for (let i = 0; i < tables.length; i++) {
	generateCalendar(tables[i], i);
}

addEventsToCalendar();
drawLeaveRectangle();



function getSumLeave(actYear) {
	let sumLeave=0;
	for (let i in eventList) {
		let date = new Date(eventList[i].startDate)
		if (eventList[i].approved >= 0 && date.getFullYear()== actYear)
				sumLeave = sumLeave + ((eventList[i].duration == 1) ? 1 : 0.5);
	}
	return sumLeave;
}


function getSumLeaveFrame(frameIndex) {
	leaveFrame = allLeaveFrame[frameIndex];
	return leaveFrame.baseLeave + leaveFrame.parentalLeave +leaveFrame.carriedLeave +leaveFrame.otherLeave;
}


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
					cell.style.border = "2px solid blue"
					cell.style.fontWeight = "bold"
				}
				cell.style.width = CELL_SIZE_X
				cell.style.height = CELL_SIZE_Y
				cell.style.userSelect = "none"
				cell.id = actYear + "." + (month+1)  + "."  + dayCounter
				if (dayCounter < 10) text = "0"; else text = ""
				text = text + String(dayCounter++);
				cell.innerText = text
				firstDayOfMonth = 0
				if (j > 5) {
					cell.style.backgroundColor = "lightGray"
				} else {
					cell.style.backgroundColor = "white"
				}
				if (day - today >= 0) {  // az aktuális dátumot megelőző napot már nem lehet módosítani
					cell.setAttribute('onclick', 'setDateAttr(this.id)')
					cell.style.cursor = "pointer"
				}
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

	for (let i in exEventList) {   // kivételnapok listája
		let date = new Date(exEventList[i].date)
		let month = date.getMonth()

		let cellId = (date.getFullYear() + '.' + (month+1) +'.' + date.getDate())
		let thisCell = document.getElementById(cellId)
		if (thisCell != null) {
			thisCell.setAttribute('title', exEventList[i].note)
			if (exEventList[i].isWorkDay) {
				thisCell.style.backgroundColor = "white"
			} else {
				thisCell.style.backgroundColor = "lightgrey"
			}
		}
	}

	for (let i in eventList) {
		let titleText="";
		let date = new Date(eventList[i].startDate)
		
		let month = date.getMonth()
		let cellId = (date.getFullYear() +'.'+ (month+1) +'.' + date.getDate())
		let thisCell = document.getElementById(cellId)
		if (thisCell != null) {
			switch (eventList[i].approved) {
				case -1: titleText="elutasítva"
					break;
				case 0: titleText = "jóváhagyásra vár"
					break;
				case 1: titleText="jóváhagyva"
					break;
			}
			switch (eventList[i].duration) {
				case 1:
					thisCell.style.backgroundColor = "lightskyblue"
					thisCell.style.color = "white"
					thisCell.setAttribute('title', titleText)
					break;
				case 2:
					thisCell.style.backgroundColor = "gold"
					thisCell.style.color = "black"
					thisCell.setAttribute('title', titleText + ' (DE)')
					break;
				case 3:
					thisCell.style.backgroundColor = "lightgreen"
					thisCell.style.color = "black"
					thisCell.setAttribute('title', titleText + ' (DU)')
					break;
				default: break;
			}
			switch (eventList[i].approved) {
				case -1:  // denied
					thisCell.style.border = "2px solid red"
					thisCell.style.color = "red"
					break;
				case 0:	// pending
					break;
				case 1:	// approved
					thisCell.style.border = "2px solid green"
					break;
				default: break;
			}
		}
	}
}

}

function exit() {

	if (!saved) {
		event.preventDefault();
		let modal=new bootstrap.Modal(document.getElementById("myModal"))
		modal.show();
	}
}



function sendrequest() {
	event.preventDefault();
	var xhr = new XMLHttpRequest();
	var rdata = eventList
	var data = JSON.stringify(rdata)
	xhr.onreadystatechange = function() {
		if (xhr.readyState == XMLHttpRequest.DONE) {
			if (xhr.status != 200) {
				document.body.innerText = 'Error: ' + xhr.responseText + '(' + xhr.status + ')';
			}
		}
	};
//	xhr.withCredentials = true;
	xhr.open('POST', '/postdata', true);
	xhr.setRequestHeader("Content-type", "application/json");
	xhr.send(data);
	saved = true;
	document.getElementById("saveBtn").disabled = saved;
}




function setDateAttr(id) {

	oCounter = leaveCounter;
	let thisCell = document.getElementById(id)
	let pos = -1;
	let date1 = new Date(id);
	date1.setHours(5)
	for (let i in eventList) {
		let date2 = new Date(eventList[i].startDate);
		date2.setHours(5)
		if (date1- date2 == 0) {
			pos = i  // már van ezen a dátumon valami az eventListben (a szabik listája)
		}
	}
	let color = thisCell.style.backgroundColor;
	if ((sumLeave - leaveCounter == .5) && (color == "white")) {
		color = 'lightskyblue';
		eventList.push({
			"id": -1,"startDate": date1, "user":null,"approved":0,"duration":2})
		pos = eventList.length - 1;
			leaveCounter++;
	}
	switch (color) {
		case 'white':  // egész nap szabi
			if (!isFreeLeave) return;
			thisCell.style.backgroundColor = "lightskyblue"
			thisCell.style.color = "white"
			thisCell.setAttribute('title', '')
			eventList.push({ "id":-1, "startDate":date1,"user":null, "approved":0, "duration":1})
			leaveCounter++;
			break;
		case 'lightskyblue':  // délelőtt (fél nap szabi)
			thisCell.style.border = "1px solid black"
			thisCell.style.backgroundColor = "gold"
			thisCell.style.color = "black"
			thisCell.setAttribute('title', 'de')
			eventList[pos].duration = 2
			leaveCounter = leaveCounter - 0.5;
			break;
		case 'gold': // délután (fél nap szabi)
			thisCell.style.border = "1px solid black"
			thisCell.style.backgroundColor = "lightgreen"
			thisCell.style.color = "black"
			thisCell.setAttribute('title', 'du')
			eventList[pos].duration = 3
			break;
		case 'lightgreen': // nem szabi
			thisCell.style.border = "1px solid black"
			thisCell.style.backgroundColor = "white"
			thisCell.style.color = "black"
			thisCell.setAttribute('title', '')
			eventList.splice(pos, 1)
			leaveCounter = leaveCounter - 0.5;
			break;
		default: break;
	}
	if (oCounter!=leaveCounter) {
		saved = false;
		document.getElementById("saveBtn").disabled = saved;
	}
	isFreeLeave = (sumLeave - leaveCounter) > 0;
	drawLeaveRectangle();

}

function drawLeaveRectangle() {  // kirajzoljuk a szabadság grafikont
	var recLength = 320;
	var weight = 35;
	var e = ((recLength- 5) / sumLeave);
	xad = e*(leaveCounter);
	xbd = e*(sumLeave-leaveCounter);
	var y = 10;
	var xa0 = 0;
	var xa1 = xa0 + xad;
	var xb0 = xa1 + 2;
	var xb1 = xb0 + xbd;
	roundRect(xa0, y - 2, xb1 + e, y + weight + 2, 5, "white")
	roundRect(xa0, y, xa1, y + weight, 5, "orange")
	roundRect(xb0, y, xb1, y + weight, 5, "lightgreen")

var textCtx = document.getElementById("leavetext");
	textCtx.innerText = "  Szabadságok: " + leaveCounter + "/" + sumLeave + " (" + (sumLeave-leaveCounter) + ")";
	textCtx.style.textAlign = "center"
	textCtx.style.fontSize = "18px"


	function roundRect(x0, y0, x1, y1, r, color) {
		var ctx = document.getElementById("rounded-rect").getContext("2d");
		var w = x1 - x0;
		var h = y1 - y0;
		if (r > w/2) r = w/2;
		if (r > h / 2) r = h / 2;
		ctx.beginPath();
		ctx.moveTo(x1 - r, y0);
		ctx.quadraticCurveTo(x1, y0, x1, y0 + r);
		ctx.lineTo(x1, y1 - r);
		ctx.quadraticCurveTo(x1, y1, x1 - r, y1);
		ctx.lineTo(x0 + r, y1);
		ctx.quadraticCurveTo(x0, y1, x0, y1 - r);
		ctx.lineTo(x0, y0 + r);
		ctx.quadraticCurveTo(x0, y0, x0 + r, y0);
		ctx.closePath();
		ctx.fillStyle = color;
		ctx.fill();


	}
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


