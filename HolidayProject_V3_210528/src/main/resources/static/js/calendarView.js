



const MONTHS = ["január", "február", "március", "április", "május", "június", "július", "augusztus", "szeptember", "október", "november", "december"]
const DAYS = ["H", "K", "SZ", "CS", "P", "SZ", "V"]



const CELL_SIZE_X = "30px"
const CELL_SIZE_Y = "20px"
const TABLE_SIZE_X ="210px"
const TABLE_SIZE_Y = "170px"
const HEAD_FONT_SIZE = "14px"
const DAY_FONT_SIZE = "10px"
const CELL_FONT_SIZE = "12px"

let tables = document.getElementsByTagName("table")
const year = new Date().getFullYear() 

var exEventList;
var eventList;


for (let i = 0; i < tables.length; i++) {
    generateCalendar(tables[i], i);
}

addEventsToCalendar();  





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
    row.style.paddingBottom = "1px"
    row.style.fontSize = HEAD_FONT_SIZE
    row.style.textAlign = "center"
    row.style.backgroundColor = "lightBlue"
    row.style.color= (month==thisMonth)? "brown":"black"
    let th = document.createElement("th")
    th.colSpan = "7"
    let dateText = ((month==0)? year + ". " : '') + MONTHS[month];
	text= document.createTextNode(dateText)
    th.appendChild(text)
    row.appendChild(th)
    row = head.insertRow();
    row.style.paddingBottom = "1px"
    row.style.fontSize = DAY_FONT_SIZE
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
    let body = table.createTBody()
    let row = body.insertRow()
    let text
    let dayCounter = 1
    let LengthOfMonth = daysInMonth(month)
    let firstDayOfMonth = new Date(year, month, 1).getDay();  // a hónap első napja milyen napra esik?
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
	            let day = new Date(year, month, dayCounter)
	            if (day - today == 0) {
	                cell.style.border = "2px solid blue"
	                cell.style.fontWeight = "bold"
					cell.setAttribute('title','Ma')
	            }
	            cell.style.width = CELL_SIZE_X
	            cell.style.height = CELL_SIZE_Y
	            cell.style.userSelect = "none"
                cell.id = year + "." + (month+1)  + "." + dayCounter
                if (dayCounter < 10) text = "0"; else text = ""
                text = text + String(dayCounter++);
                cell.innerText = text
                firstDayOfMonth = 0
                if (j > 5) {
                    cell.style.backgroundColor = "lightGray"
                } else {
                    cell.style.backgroundColor = "white"
                    }
              
            } else {
                cell.innerText = "";
                cell.style.border = "0px"
            }
        }
    }
}

function daysInMonth(month) {
    return new Date(year, month +1 , 0).getDate()
}



function addEventsToCalendar() {
	
	for (let i in exEventList){   // kivételnapok listája
		let date = new Date(exEventList[i].date)
		let actYear = date.getFullYear();
		if(actYear == year){
			let month = date.getMonth() 
			
			let cellId=(actYear +'.'+ (month+1) +'.' + date.getDate())
			let thisCell= document.getElementById(cellId)
			thisCell.setAttribute('title', exEventList[i].note)
			if (exEventList[i].isWorkDay) {
				thisCell.style.backgroundColor = "GoldenRod"
				thisCell.style.color = "black"
				
			} else {
				thisCell.style.backgroundColor = "SlateGrey"
				thisCell.style.color = "lightGrey"
			}
		}
	}
	
	for (let i in eventList){
		let date = new Date(eventList[i].startDate)
		actYear = date.getFullYear();
		if(actYear == year){
			let titleText="";
			if (actYear > year) return;
			
			let month = date.getMonth() 
			let cellId=(actYear +'.'+ (month+1) +'.' + date.getDate())
			let thisCell= document.getElementById(cellId)
			switch (eventList[i].approved){
				case -1: titleText="elutasítva"
				break;
				case 0: titleText="jóváhagyásra vár"
				break;
				case 1: titleText="jóváhagyva"
				break;
			}
			switch(eventList[i].duration) {
				case 1:
				thisCell.style.backgroundColor = "lightskyblue"
	            thisCell.style.color = "white"
				thisCell.setAttribute('title', titleText)
				break;
				case 2:
				 thisCell.style.backgroundColor = "yellow"
	            thisCell.style.color = "black"
	            thisCell.setAttribute('title', titleText+' (DE)')
				break;
				case 3:
				thisCell.style.backgroundColor = "lightgreen"
	            thisCell.style.color = "black"
	            thisCell.setAttribute('title', titleText+' (DU)')
				break;
				default: break;
				}
			switch(eventList[i].approved) {
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


