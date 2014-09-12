/*--------------------------------------------------|
| dTree 2.05 | www.destroydrop.com/javascript/tree/ |
|---------------------------------------------------|
| Copyright (c) 2002-2003 Geir Landr�               |
|                                                   |
| This script can be used freely as long as all     |
| copyright messages are intact.                    |
|                                                   |
| Updated: 17.04.2003 | extends by Jerry Yao        |
|---------------------------------------------------|
| extends: 1)can be clicked to redirect another url |
|          2)can be selected only one treeNode  	|
|          3)can be checked more than one treeNode  |
| Updated: 30.01.2008 | extends by Jerry Yao        |
|--------------------------------------------------*/

// Node object
function Node(id, pid, name, url, title, target, icon, iconOpen, open) {
	this.id = id; // node's Id
	this.pid = pid; // node's parentId
	this.name = name; // node's name
	this.url = url ; // wsyao,the url of the node
	this.title = title; // the tooltip of the node
	this.target = target; // the target of the url
	this.icon = icon; // this will be the icon's src or use default when this is null
	this.iconOpen = iconOpen; // if the node is opened,this will be the picture's src or use default when this is null
	this._io = open || false; // boolean type,this node is opened or not
	this._ic = false; // wsyao (this node is checked or not)
	this._is = false; // is selected or not
	this._ls = false; // last sibling
	this._hc = false; // has child or not
	this._ai = 0;
	this._p = null;
}

// Tree object
// wsyao
// selectNodeNum: 0,only link ; 1,select one node only; 2,can be checked more than one treeNode
function dTree(objName,contextPath,selectNodeNum) {
	this.config = {
		target              : null,
		folderLinks         : true,
		canMutiCheck		: (parseInt(selectNodeNum) >  1) || false, 	// wsyao (this tree can select more than one node or not)
		useSelectOne		: (parseInt(selectNodeNum) == 1) || false,	// wsyao , if true,can only select one node
		useSelection		: null,										// wsyao (this.config.useSelection = !this.config.canMutiCheck)
		useCookies			: true,
		useLines            : true,
		useIcons            : true,
		useStatusText		: false,
		closeSameLevel      : false,
		inOrder             : false

	};
	this.icon = {
		root				: contextPath +'img/base.gif',
		folder			    : contextPath +'img/folder.gif',
		folderOpen          : contextPath +'img/folderopen.gif',
		node				: contextPath +'img/page.gif',
		empty				: contextPath +'img/empty.gif',
		line				: contextPath +'img/line.gif',
		join				: contextPath +'img/join.gif',
		joinBottom          : contextPath +'img/joinbottom.gif',
		plus				: contextPath +'img/plus.gif',
		plusBottom          : contextPath +'img/plusbottom.gif',
		minus				: contextPath +'img/minus.gif',
		minusBottom         : contextPath +'img/minusbottom.gif',
		nlPlus              : contextPath +'img/nolines_plus.gif',
		nlMinus             : contextPath +'img/nolines_minus.gif'
	};
	this.obj = objName;
	this.aNodes = [];
	this.aIndent = [];
	this.root = new Node(-1);
	this.selectedNode = null;
	this.selectedFound = false;
	this.completed = false;
	this.config.useSelection = !this.config.canMutiCheck; // wsyao
	this.canLink = (!this.config.canMutiCheck && !this.config.useSelectOne); // wsyao, canLink only
}

// Adds a new node to the node array
dTree.prototype.add = function(id, pid, name, url, title, target, icon, iconOpen, open) {
	this.aNodes[this.aNodes.length] = new Node(id, pid, name, url, title, target, icon, iconOpen, open);
};

// Open/close all nodes
dTree.prototype.openAll = function() {
	this.oAll(true);
};
dTree.prototype.closeAll = function() {
	this.oAll(false);
};

// Outputs the tree to the page
dTree.prototype.toString = function() {
	var str = '<div class="dtree">\n';
	if (document.getElementById) {
		if (this.config.useCookies) {
			this.selectedNode = this.getSelected();
		}
		str += this.addNode(this.root);
	} else{
		str += 'Browser not supported.';
	}
	str += '</div>';
	if (!this.selectedFound) {
		this.selectedNode = null;
	}
	this.completed = true;
	return str;
};

// Creates the tree structure
dTree.prototype.addNode = function(pNode) {
	var str = '';
	var n=0;
	if (this.config.inOrder) {n = pNode._ai;}
	for (n; n<this.aNodes.length; n++) {
		if (this.aNodes[n].pid == pNode.id) {
			var cn = this.aNodes[n]; // cn, current node
			cn._p = pNode;
			cn._ai = n;
			this.setCS(cn);
			if (!cn.target && this.config.target) {cn.target = this.config.target;}
			if (cn._hc && !cn._io && this.config.useCookies) {cn._io = this.isOpen(cn.id);}
			if (!this.config.folderLinks && cn._hc) {cn.url = null;}
			if (!this.canLink) {cn.url = null;}
			if (this.config.useSelection && cn.id == this.selectedNode && !this.selectedFound) {
					cn._is = true;
					this.selectedNode = n;
					this.selectedFound = true;
			}
			str += this.node(cn, n);
			if (cn._ls) {break;}
		}
	}
	return str;
};

// Creates the node icon, url and text
dTree.prototype.node = function(node, nodeId) {
	var str = '<div class="dTreeNode">' + this.indent(node, nodeId);
	if (this.config.useIcons) {
		if (!node.icon) {node.icon = (this.root.id == node.pid) ? this.icon.root : ((node._hc) ? this.icon.folder : this.icon.node);}
		if (!node.iconOpen) {node.iconOpen = (node._hc) ? this.icon.folderOpen : this.icon.node;}
		if (this.root.id == node.pid) {
			node.icon = this.icon.root;
			node.iconOpen = this.icon.root;
		}
		str += '<img id="i' + this.obj + nodeId + '" src="' + ((node._io) ? node.iconOpen : node.icon) + '" alt="" />';
		// wsyao
		if (this.config.canMutiCheck){
			str += '<input id="in' + this.obj + nodeId + '" type="checkbox" onclick="javascript: ' + this.obj + '.c(' + nodeId + ');"' + ' />';
		} 
		
	}
	if (node.url){
		str += '<a id="s' + this.obj + nodeId + '" class="' + ((this.config.useSelection) ? ((node._is ? 'nodeSel' : 'node')) : 'node') + '" href="' + node.url + '"';
		// wsyao //if (node.title) str += ' title="' + node.title + '"';
		str += ' title="' + (node.title ? node.title : node.name ) + '"'; 
		if (node.target)
			{str += ' target="' + node.target + '"';}
		if (this.config.useStatusText)
			{str += ' onmouseover="window.status=\'' + node.name + '\';return true;" onmouseout="window.status=\'\';return true;" ';}
		
		if (this.config.useSelection && ((node._hc && this.config.folderLinks) || !node._hc)) {
			str += ' onclick="javascript: ' + this.obj + '.s(' + nodeId + ');"';
		} 
		str += '>';
	} else if ((!this.config.folderLinks || !node.url) && node._hc && node.pid != this.root.id) {
		str += '<a href="javascript: ' + this.obj + '.o(' + nodeId + ');" class="node">';
	} else if(this.config.useSelectOne && (!node.url && !node._hc && node.pid != this.root.id)) { //wsyao added
		str += '<a id="s' + this.obj + nodeId + '" class="' + ((this.config.useSelection) ? ((node._is ? 'nodeSel' : 'nodeNoUrl')) : 'nodeNoUrl') + '" ';
		str += ' title="' + (node.title ? node.title : node.name ) + '"'; 
		str += ' href="javascript: ' + this.obj + '.s(' + nodeId + ');" class="node">';
	}
	
//	if (node.url){
//		str += '<a id="s' + this.obj + nodeId + '" class="' + ((this.config.useSelection) ? ((node._is ? 'nodeSel' : 'node')) : 'node') + '" href="' + node.url + '"';
//		// wsyao //if (node.title) str += ' title="' + node.title + '"';
//		str += ' title="' + (node.title ? node.title : node.name ) + '"'; 
//	}
//	if (node.url && node.target)
//		{str += ' target="' + node.target + '"';}
//	if (node.url && this.config.useStatusText)
//		{str += ' onmouseover="window.status=\'' + node.name + '\';return true;" onmouseout="window.status=\'\';return true;" ';}
//	
//	if (this.config.useSelection && ((node._hc && this.config.folderLinks) || !node._hc)) {
//		if(node.url){
//			str += ' onclick="javascript: ' + this.obj + '.s(' + nodeId + ');">';
//			str += '>';
//		}
//	} 
//	
//	if(!node.url){
//		if ((!this.config.folderLinks || !node.url) && node._hc && node.pid != this.root.id) {
//			str += '<a href="javascript: ' + this.obj + '.o(' + nodeId + ');" class="node">';
//		}
//	}
	
	str += node.name;
	
	//wsyao //if (node.url || ((!this.config.folderLinks || !node.url) && node._hc)) 
	if (node.url || ((!this.config.folderLinks || !node.url) && node._hc)) {
		str += '</a>';
	} else if (this.config.useSelectOne && (!node.url && !node._hc)){ //wsyao added
		str += '</a>';
	}
	str += '</div>';
	if (node._hc) {
		str += '<div id="d' + this.obj + nodeId + '" class="clip" style="display:' + ((this.root.id == node.pid || node._io) ? 'block' : 'none') + ';">';
		str += this.addNode(node);
		str += '</div>';
	}
	this.aIndent.pop();
	return str;
};

// Adds the empty and line icons
dTree.prototype.indent = function(node, nodeId) {
	var str = '';
	if (this.root.id != node.pid) {
		for (var n=0; n<this.aIndent.length; n++){
			str += '<img src="' + ( (this.aIndent[n] == 1 && this.config.useLines) ? this.icon.line : this.icon.empty ) + '" alt="" />';
		}
		(node._ls) ? this.aIndent.push(0) : this.aIndent.push(1);
		if (node._hc) {
			str += '<a href="javascript: ' + this.obj + '.o(' + nodeId + ');"><img id="j' + this.obj + nodeId + '" src="';
			if (!this.config.useLines) {
				str += (node._io) ? this.icon.nlMinus : this.icon.nlPlus;
			} else {
				str += ( (node._io) ? ((node._ls && this.config.useLines) ? this.icon.minusBottom : this.icon.minus) : ((node._ls && this.config.useLines) ? this.icon.plusBottom : this.icon.plus ) );
			}
			str += '" alt="" /></a>';
		} else {
			str += '<img src="' + ( (this.config.useLines) ? ((node._ls) ? this.icon.joinBottom : this.icon.join ) : this.icon.empty) + '" alt="" />';
		}
	}
	return str;
};

// Checks if a node has any children and if it is the last sibling
dTree.prototype.setCS = function(node) {
	var lastId;
	for (var n=0; n<this.aNodes.length; n++) {
		if (this.aNodes[n].pid == node.id) node._hc = true;
		if (this.aNodes[n].pid == node.pid) lastId = this.aNodes[n].id;
	}
	if (lastId==node.id) node._ls = true;
};

// Returns the selected node
dTree.prototype.getSelected = function() {
	var sn = this.getCookie('cs' + this.obj);
	return (sn) ? sn : null;
};

// Highlights the selected node
dTree.prototype.s = function(id) {
	if (!this.config.useSelection) return;
	var cn = this.aNodes[id];
	if (cn._hc && !this.config.folderLinks) return;
	if (this.selectedNode != id ) {
		if (this.selectedNode || this.selectedNode==0) {
			eOld = document.getElementById("s" + this.obj + this.selectedNode);
			eOld.className = (this.aNodes[this.selectedNode].url) ? "node" : "nodeNoUrl"; 	// wsyao // eOld.className = "node";
		}
		eNew = document.getElementById("s" + this.obj + id);
		eNew.className = "nodeSel";
		this.selectedNode = id;
		if (this.config.useCookies) this.setCookie('cs' + this.obj, cn.id);
	}
};


// Toggle Open or close
dTree.prototype.o = function(id) {
	var cn = this.aNodes[id];
	this.nodeStatus(!cn._io, id, cn._ls);
	cn._io = !cn._io;
	if (this.config.closeSameLevel) this.closeLevel(cn);
	if (this.config.useCookies) this.updateCookie();
};

// Open or close all nodes
dTree.prototype.oAll = function(status) {
	for (var n=0; n<this.aNodes.length; n++) {
		if (this.aNodes[n]._hc && this.aNodes[n].pid != this.root.id) {
			this.nodeStatus(status, n, this.aNodes[n]._ls)
			this.aNodes[n]._io = status;
		}
	}
	if (this.config.useCookies) this.updateCookie();
};

// Opens the tree to a specific node
dTree.prototype.openTo = function(nId, bSelect, bFirst) {
	if (!bFirst) {
		for (var n=0; n<this.aNodes.length; n++) {
			if (this.aNodes[n].id == nId) {
				nId=n;
				break;
			}
		}
	}
	var cn=this.aNodes[nId];
	if (cn.pid==this.root.id || !cn._p) return;
	cn._io = true;
	cn._is = bSelect;
	if (this.completed && cn._hc) this.nodeStatus(true, cn._ai, cn._ls);
	if (this.completed && bSelect) this.s(cn._ai);
	else if (bSelect) this._sn=cn._ai;
	this.openTo(cn._p._ai, false, true);
};

// Closes all nodes on the same level as certain node
dTree.prototype.closeLevel = function(node) {
	for (var n=0; n<this.aNodes.length; n++) {
		if (this.aNodes[n].pid == node.pid && this.aNodes[n].id != node.id && this.aNodes[n]._hc) {
			this.nodeStatus(false, n, this.aNodes[n]._ls);
			this.aNodes[n]._io = false;
			this.closeAllChildren(this.aNodes[n]);
		}
	}
}

// Closes all children of a node
dTree.prototype.closeAllChildren = function(node) {
	for (var n=0; n<this.aNodes.length; n++) {
		if (this.aNodes[n].pid == node.id && this.aNodes[n]._hc) {
			if (this.aNodes[n]._io) this.nodeStatus(false, n, this.aNodes[n]._ls);
			this.aNodes[n]._io = false;
			this.closeAllChildren(this.aNodes[n]);		
		}
	}
}


// Change the status of a node(open or closed)
dTree.prototype.nodeStatus = function(status, id, bottom) {
	eDiv	= document.getElementById('d' + this.obj + id);
	eJoin	= document.getElementById('j' + this.obj + id);
	if (this.config.useIcons) {
		eIcon	= document.getElementById('i' + this.obj + id);
		eIcon.src = (status) ? this.aNodes[id].iconOpen : this.aNodes[id].icon;
	}
	eJoin.src = (this.config.useLines)?
	((status)?((bottom)?this.icon.minusBottom:this.icon.minus):((bottom)?this.icon.plusBottom:this.icon.plus)):
	((status)?this.icon.nlMinus:this.icon.nlPlus);
	eDiv.style.display = (status) ? 'block': 'none';
};


// [Cookie] Clears a cookie
dTree.prototype.clearCookie = function() {
	var now = new Date();
	var yesterday = new Date(now.getTime() - 1000 * 60 * 60 * 24);
	this.setCookie('co'+this.obj, 'cookieValue', yesterday);
	this.setCookie('cs'+this.obj, 'cookieValue', yesterday);
	//this.setCookie('cci'+this.obj, 'cookieValue', yesterday);	// wsyao
	//this.setCookie('ccn'+this.obj, 'cookieValue', yesterday);	// wsyao
};

// [Cookie] Sets value in a cookie
dTree.prototype.setCookie = function(cookieName, cookieValue, expires, path, domain, secure) {
	document.cookie =
		escape(cookieName) + '=' + escape(cookieValue)
		+ (expires ? '; expires=' + expires.toGMTString() : '')
		+ (path ? '; path=' + path : '')
		+ (domain ? '; domain=' + domain : '')
		+ (secure ? '; secure' : '');
};

// [Cookie] Gets a value from a cookie
dTree.prototype.getCookie = function(cookieName) {
	var cookieValue = '';
	var posName = document.cookie.indexOf(escape(cookieName) + '=');
	if (posName != -1) {
		var posValue = posName + (escape(cookieName) + '=').length;
		var endPos = document.cookie.indexOf(';', posValue);
		if (endPos != -1) cookieValue = unescape(document.cookie.substring(posValue, endPos));
		else cookieValue = unescape(document.cookie.substring(posValue));
	}
	return (cookieValue);
};

// [Cookie] Returns ids of open nodes as a string
dTree.prototype.updateCookie = function() {
	var str = '';
	for (var n=0; n<this.aNodes.length; n++) {
		if (this.aNodes[n]._io && this.aNodes[n].pid != this.root.id) {
			if (str) str += '.';
			str += this.aNodes[n].id;
		}
	}

};

// [Cookie] Checks if a node id is in a cookie
dTree.prototype.isOpen = function(id) {
	var aOpen = this.getCookie('co' + this.obj).split('.');
	for (var n=0; n<aOpen.length; n++)
		if (aOpen[n] == id) return true;
	return false;
};

// If Push and pop is not implemented by the browser
if (!Array.prototype.push) {
	// add one argument or more arguments to the Array,then return new length of the Array 
	Array.prototype.push = function array_push() {
		for(var i=0;i<arguments.length;i++)
			this[this.length]=arguments[i];
		return this.length;
	}
};
if (!Array.prototype.pop) {
	// return the Last Element of the Array and remove it
	Array.prototype.pop = function array_pop() {
		lastElement = this[this.length-1];
		this.length = Math.max(this.length-1,0);
		return lastElement;
	}
};



// -----------------------------------------------------start 


// wsyao
// Returns the checked node's Ids
dTree.prototype.getCheckedIds = function() {
	var str = '';
	// can check more than one node
	if(this.config.canMutiCheck){
		for (var n=0; n<this.aNodes.length; n++) {
			if(this.aNodes[n]._ic && !this.aNodes[n]._hc) {
					if(str){
						str += ',';
					}
					str += this.aNodes[n].id;
			}
		}
	} else { // can only select one node
		if(this.selectedNode != null){
				return this.aNodes[this.selectedNode].id;
		}
	}

	return str;
};

// wsyao
// Returns the checked node's Names
dTree.prototype.getCheckedNames = function() {
	var str = '';
	// can check more than one node
	if(this.config.canMutiCheck){
		for (var n=0; n<this.aNodes.length; n++) {
			if(this.aNodes[n]._ic && !this.aNodes[n]._hc) {
					if(str){
						str += ',';
					}
					str += this.aNodes[n].name;
			}
		}
	} else { // can only select one node
		if(this.selectedNode != null){
				return this.aNodes[this.selectedNode].name;
		}
	}
	return str;
};


//wsyao
// init the tree 
dTree.prototype.initCheck = function(ids) {
	if(ids == ""){
		return ;	
	} else {
		if(this.config.canMutiCheck){
			var aCheck = ids.split(',');
			for (var n=0; n<this.aNodes.length; n++) {
				for(var i=0; i<aCheck.length; i++){
					if(this.aNodes[n].id == aCheck[i]){
						this.aNodes[n]._ic = true;
						this.nodeCheckStatus(true,n);
						this.revertAllParent(this.aNodes[n]);
					}
				}
			}
			return ;
		} else if (this.config.useSelectOne) {
			var aCheck = ids.split(',');
			for (var n=0; n<this.aNodes.length; n++) {
				if(this.aNodes[n].id == aCheck[0]){
					if(!this.aNodes[n]._hc){
						this.s(n);
						this.aNodes[n]._is = true;
					}
					break;
				}
			}
			return ;
		}
	}
};

// wsyao
// Toggle Checked or not
dTree.prototype.c = function(id) {
	var cn = this.aNodes[id];

	if (cn._ic) {
		this.cancelAllChildren(cn);
		
		// change own
		this.nodeCheckStatus(false,id);
		cn._ic = false;
		
		this.cancelAllParent(cn);
	} else {
		this.revertAllChildren(cn);
		
		// change own
		this.nodeCheckStatus(true,id);
		cn._ic = true;
		
		this.revertAllParent(cn); 
	}

};

// wsyao
// revert all children of a node
dTree.prototype.cancelAllChildren = function(node) {
	for (var n=0; n<this.aNodes.length; n++) {
		if (this.aNodes[n].pid == node.id) {
			this.nodeCheckStatus(false, n);
			this.aNodes[n]._ic = false;
			this.cancelAllChildren(this.aNodes[n]);		
		}
	}
}
// wsyao
dTree.prototype.revertAllChildren = function(node) {
	for (var n=0; n<this.aNodes.length; n++) {
		if (this.aNodes[n].pid == node.id) {
			this.nodeCheckStatus(true, n);
			this.aNodes[n]._ic = true;
			this.revertAllChildren(this.aNodes[n]);		
		}
	}
}
// wsyao
dTree.prototype.cancelAllParent = function(node){
	for (var n=0; n<this.aNodes.length; n++) {
		if (this.aNodes[n].id == node.pid) {
			this.nodeCheckStatus(false, n);
			node._ic = false;
			this.cancelAllParent(this.aNodes[n]);		
		}
	}	
}


//wsyao
// revert all Parent of a node
dTree.prototype.revertAllParent = function(node){
	for (var n=0; n<this.aNodes.length; n++) {
		if (this.aNodes[n].id == node.pid) {
			this.aNodes[n]._ic = this.isAllChildrenChecked(this.aNodes[n]);
			this.nodeCheckStatus(this.aNodes[n]._ic, n);
			
			this.revertAllParent(this.aNodes[n]);
		}
	}	
}

// wsyao
// Change the status of a node(checked or not)
dTree.prototype.nodeCheckStatus = function(status, id) {
	if (this.config.canMutiCheck) {
		var eInput = document.getElementById('in' + this.obj + id);
		eInput.checked = status;
	}
};

// wsyao
// all children are checked ,return true;
dTree.prototype.isAllChildrenChecked = function(node){
		for (var n=0; n<this.aNodes.length; n++) {
			if (this.aNodes[n].pid == node.id) {
				if(!this.aNodes[n]._ic || !this.isAllChildrenChecked(this.aNodes[n])) {
					return false;
				}
			}
		}
		return true;
}
// ----------------------------------------------------------------------end

// ~-------------------------------------------- [below are the ApI]
	// the object to store the location
	function CPos(x, y) {
		this.x = x;
		this.y = y;
	}
	
	// get the INPUT 's position
	function GetObjPos(ATarget)
	{
		var target = ATarget;
		var pos = new CPos(target.offsetLeft, target.offsetTop);
		
		var target = target.offsetParent;
		while (target) {
			pos.x += target.offsetLeft;
			pos.y += target.offsetTop;
			target = target.offsetParent
		}
		return pos;
	}

	// get the point's position
	/*function mouseLocation(e) {
		var ns4 = document.layers;
		var ns6 = document.getElementById && !document.all;
		var ie4 = document.all;
		if(ns4||ns6){
			x = e.pageX;
			y = e.pageY;
		}
		else {
			x = event.x + document.body.scrollLeft;
			y = event.y + document.body.scrollTop;
		}
		var mouseLocation = new CPos(x, y);
		return mouseLocation;
	}*/
	
	// hide the tree onMouseDown, eg : document.onmousedown = hideTree; (not good)
	/*function hideTree(e){
		var treeName  = "XXXXXXXXX";
		var tree_Container = treeName + "_treeScript_Container" ;
		var container = $(tree_Container);
		var outFlag = false;
		var ns4 = document.layers;
		var ns6 = document.getElementById && !document.all;
		var ie4 = document.all;
		if(ns4||ns6){
			x = e.pageX;
			y = e.pageY;
		}
		else {
			x = event.x + document.body.scrollLeft;
			y = event.y + document.body.scrollTop;
		}
		var x = window.event.x+document.documentElement.scrollLeft;
		var y = window.event.y+document.documentElement.scrollTop;
		var xx = container.style.left.replace("px","");
		var yy = container.style.top.replace("px","");
		var width = eval(container.style.width.replace("px",""));
		var height = eval(container.style.height.replace("px",""));
		 window.status = "x:" + x + " y:" + y + " xx:" + xx + " yy:" + yy ;
		if (x < eval(xx))  outFlag=true;
		if (y < eval(yy))  outFlag=true;
		if (x > ( width  + eval(xx) ))  outFlag=true;
		if (y > ( height + eval(yy) ))  outFlag=true;
		if(outFlag) container.style.display="none";
	}*/
	
	//document.onmousedown = hideTree;
	 
	 // click to drop down the tree 
	function treeDropDown(oTargetElement,treeName){
		var treeData = treeName + "_Div";
		var tree  = treeName + "_treeScript";
		if($(tree).innerHTML == ""){
				$(tree).innerHTML = $(treeData).innerHTML;
		}
		
		var names = treeName + "_text" ;
		//var checkedNames = $(names); 
		var ids = treeName + "_hidden" ; 
		var checkedIds = $(ids)
		eval(treeName).initCheck(checkedIds.value);
		showTree(oTargetElement,treeName);
	}
	
	function showTree(oTargetElement,treeName) {
		var tree_Container = treeName + "_treeScript_Container" ;
		var tree_Title = treeName + "_treeScript_Title" ;
		var container = $(tree_Container); 
		var treeTitle = $(tree_Title); 
		var pos = GetObjPos(oTargetElement);
		
		treeTitle.style.left = pos.x;
		treeTitle.style.top = pos.y + 25;
		treeTitle.style.height = 15;
		treeTitle.style.width = 130;
		treeTitle.style.display = 'block';  
		
		container.style.left = pos.x;
		container.style.top = pos.y + 25 + 15;
		container.style.height = 180;
		container.style.width = 130;
		container.style.overflowY = 'auto';
		container.style.overflowX = 'auto';
		container.style.display = 'block'; 
		
	}
	
	function hideTree(treeName){
		var tree_Container = treeName + "_treeScript_Container" ;
		var tree_Title = treeName + "_treeScript_Title" ;
		
		var container = $(tree_Container); 
		var treeTitle = $(tree_Title); 
		container.style.display = 'none';
		treeTitle.style.display = 'none'; 
		
		var names = treeName + "_text" ;
		var ids = treeName + "_hidden" ; 
		var checkedNames = $(names); 
		var checkedIds = $(ids); 
		
		// change INPUTs with selected leaf node(s)
		checkedNames.value = eval(treeName).getCheckedNames();
		checkedIds.value =  eval(treeName).getCheckedIds();
		
		// document.write($(myTree_treeScript).innerHTML);
	}