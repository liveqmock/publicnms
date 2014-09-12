var rowNum = 0;
var action = "";
function setMobile(name) {

	currentName = name;

	var currentName = document.getElementById(name);

	var url = action + "&event=" + currentName.id + "&value="
			+ currentName.value + "";
	msgWindow = window
			.open(
					url,
					"protypeWindow",
					"toolbar=no,width=600,height=400,directories=no,status=no,scrollbars=yes,menubar=no");
}

function addRow(timeShareConfigDivId, timeShareType) {
	var tr = document.getElementById(timeShareConfigDivId).insertRow(0);
	rowNum = rowNum + 1;
	var str = "" + rowNum;
	while (str.length < 2) {
		str = "0" + str;
	}
	var hours = "";
	for (i = 0; i <= 24; i++) {
		hours = hours + '<OPTION value="' + i + '">' + i + '</OPTION>';
	}
	var td = tr.insertCell(tr.cells.length);

	var elemTable = document.createElement("table");
	var elemTBody = document.createElement("tBody");

	elemTable.id = "table" + str;
	elemTable.width = "100%";
	elemTable.align = "left";
	elemTable.cellPadding = "1";
	elemTable.cellSpacing = "1";
	elemTable.bgColor = "black";
	elemTBody.bgColor = "white";
	elemTable.appendChild(elemTBody);
	td.appendChild(elemTable);

	var tr0 = document.createElement("tr");
	elemTBody.appendChild(tr0);
	var td00 = tr0.insertCell(tr0.cells.length);
	td00.className = "lab";
	td00.innerHTML = '<input id="timeShareConfigId' + str
			+ '" name="timeShareConfigId' + str + '" type="hidden"  />'
			+ '<input id="timeShareType' + str + '" name="timeShareType' + str
			+ '" type="hidden"  /><span class="must">*</span>开始时间';
	var td01 = tr0.insertCell(tr0.cells.length);
	td01.align = "left";
	td01.innerHTML = '<select id="beginTime' + str + '" name="beginTime' + str
			+ '" style="width:100px">' + hours + '</select>';
	var td02 = tr0.insertCell(tr0.cells.length);
	td02.className = "lab";
	td02.innerHTML = '<span class="must">*</span>结束时间';
	var td03 = tr0.insertCell(tr0.cells.length);
	td03.align = "left";
	td03.innerHTML = '<select id="endTime' + str + '" name="endTime' + str
			+ '" style="width:100px">' + hours + '</select>';
	var td04 = tr0.insertCell(tr0.cells.length);
	td04.className = "lab";
	td04.innerHTML = '<span class="must">*</span>接收人';
	var td05 = tr0.insertCell(tr0.cells.length);
	td05.align = "left";
	td05.innerHTML = '<input class="input-text" readonly="readonly" id="userIds'
			+ str
			+ '" name="userIds'
			+ str
			+ '" type="text"  size="20"  onclick="setMobile(this.id)" />';
	var td06 = tr0.insertCell(tr0.cells.length);
	td06.className = "lab";
	td06.innerHTML = '<a href="javascript:delRow(' + timeShareConfigDivId + ','
			+ rowNum + ')">删除</a>';
	document.getElementById("rowNum").value = rowNum;
	document.getElementById("timeShareType"+str).value = timeShareType;
}

function delRow(timeShareConfigDivId, rowNo) {
	
	var str = "" + rowNo;
	while (str.length < 2) {
		str = "0" + str;
	}
	var i = 0;
	
	while (timeShareConfigDivId.rows[i].firstChild.firstChild.id != "table"
			+ str) {
		i++;
	}
	
	timeShareConfigDivId.deleteRow(i);
}

function timeShareConfig(timeShareConfigDivId, store) {
	for (var i = 0; i < store.length; i++) {
		var item = store[i];
		addRow(timeShareConfigDivId, item.timeShareType);
		var str = "" + rowNum;
		if (str.length < 2) {
			str = "0" + str;
		}
		document.getElementById("timeShareConfigId" + str).value = item.timeShareConfigId;
		document.getElementById("timeShareType" + str).value = item.timeShareType;
		document.getElementById("beginTime" + str).value = item.beginTime;
		document.getElementById("endTime" + str).value = item.endTime;
		document.getElementById("userIds" + str).value = item.userIds;
	}
}
