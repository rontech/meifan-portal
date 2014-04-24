var xhr;
var j = -1; //记录当前列表被选中的编号

function getIdOBJ(str){
	return document.getElementById(str);
}

function hideSearch(){
	getIdOBJ("dSuggest").style.display = "none";
}

//获取XMLHttpRequest对象
function getXmlHttpObject() {
    var xmlHttp = null;
    try {
        // Firefox, Opera 8.0+, Safari
        xmlHttp = new XMLHttpRequest();
    } catch (e) {
        // Internet Explorer
        try {
           xmlHttp = new ActiveXObject("Msxml2.XMLHTTP");
        } catch (e) {
           xmlHttp = new ActiveXObject("Microsoft.XMLHTTP");
        }
    }
    return xmlHttp;
 }

//测试之用
function test() {
	alert("有反应");
}

//获取按键的代号
function getKeyCode(e) {
	var keycode;
	if (window.event) {//通过测试Google Chrome和IE都是调用这句
		keycode = e.keyCode;
	} else if (e.which) {//FireFox调用这句
		keycode = e.which;
	}
	return keycode;
}


//核心方法，ajax获取数据库数据
function getSuggest(keyword, e) {
	
	var keycode = getKeyCode(e);
	//40是down，38是up， 13是Enter
	
	if (keycode == 40 || keycode == 38 || keycode == 13) {
		return;
	}

	//escape()是对字符串进行编码，转化为16进制数据，类似java中的native2ascii
	if (null == escape(keyword) || "" == escape(keyword)) {
		//如果没有输入内容或者输入的内容是空的，则不现实任何东西
		getIdOBJ("dSuggest").innerHTML = "";
		return;
	}

	/*
	open(method,url,async):	规定请求的类型、URL 以及是否异步处理请求。
							method：请求的类型；GET 或 POST
							url：文件在服务器上的位置
							async：true（异步）或 false（同步）
	send(string): 		将请求发送到服务器。
						string：仅用于 POST 请求
	 */
	 var xhr = getXmlHttpObject();//取得xhr对象
	 
	 var url = "/getkeyWordsByajax/"+keyword;
	 xhr.open("get", url, true);
	
	//设置回调函数
	xhr.onreadystatechange = function() {
		//如果一切正常就执行一下代码
		if (xhr.readyState == 4 && xhr.status == 200) {
			//为div设置内容
			//alert(xhr.responseText);
			var tips = xhr.responseText.split(',');
			var inserthtml = '<ul id="sug">';
			if(tips.length!=0){
                for(var i=0;i<tips.length;i++){
                    if(tips[i] !=""){
                	inserthtml = inserthtml+"<li onkeydown=\'if(getKeyCode(event) == 13)form_submit();\' onmouseover=\'theMouseOver(" + i + ");\' onmouseout=\'theMouseOut("+ i +")';\ onclick=\'theMouseClick("+ i +");'\>" + tips[i] + "</li>";

                    }
                }   
            }
            inserthtml = inserthtml + '</ul>';
            getIdOBJ("dSuggest").innerHTML = inserthtml;
			//调用showDiv()；
			showDiv();
		}
	};
	//创建一条HTTP请求
	//发送创建的请求
	xhr.send(null);
}

//设置div为可见
function showDiv() {
	getIdOBJ("dSuggest").style.display = "block";
}


//为<li>设置样式，设置选中高亮
function set_style(num) {
	//获得所有的li标签
	var li = getIdOBJ("sug").getElementsByTagName("li");
	//遍历所有的li标签，找到指定的编号的标签
	for ( var i = 0; i < li.length; i++) {
		if (i >= 0 && i < li.length && i == num) {
			//找到指定的标签，为其设置样式
			li[i].className = "select";
			//并且选择到该项的时候为搜索框设置该项的值
			
		} else {
			//如果没有找到，啥都不做
			li[i].className = "";
		}
	}
}

//按down和up键时的操作（键盘选择）
function updown(e) {
	
	// 得到按键代号
	var keycode = getKeyCode(e);
	//获取li节点对象
	var li = getIdOBJ("sug").getElementsByTagName("li");
	if(keycode == 40 || keycode == 38) {
		
		if (li == null) {//如果没有选项，直接返回
			return;
		}
		
		//如果是按的down键
		if(keycode == 40) {
			//将j加1
			j++;
			if (j >= li.length) {//如果已经到达最底部，则回到第一项
				j = 0;
			}
		} else if (keycode == 38) { //如果按的是up键
			j--;
			if (j == -1) {//如果到达最顶端，则返回到最后一项
				j = li.length - 1;
			}
		}
		//将选中项的值设置到文本框中
		getIdOBJ("keyword").value = li[j].innerHTML;
		//为选定的项设置样式
		set_style(j);
	}
}

//鼠标经过时的操作，此处的pos在servlet中传入
function theMouseOver(pos) {
	set_style(pos);
	j = pos;
}

//鼠标离开时的操作
function theMouseOut(pos) {
	var li = getIdOBJ("sug").getElementsByTagName("li");
	//当鼠标离开之后li上没有样式
	li[pos].className = "";
}

//此处定义函数模仿jQuery，根据ID获取属性
//function $(id) {
	//return document.getElementById(id);
//}

//获取指定节点的位置
function getPos(el, sProp) {
	var iPos = 0;
	while (el != null) {
		//找到指定的位置的坐标(上top、下bottom、左left、右right)
		iPos += el["offset" + sProp];
		//找到一个元素位置（一般为<body>）作为坐标的原点
		el = el.offsetParent;
	}
	return iPos;
}

//设置为指定的元素设置位置, 此处obj为节点对象比如：调用$("keyword")传入方法内，例如：setPosition($('keyword'))
function setPosition(obj) {
	//此处需要将sSuggest的css设置position：absolute才能有效
	//将dSuggest的css位置设置左边与$("keyword")对齐
	getIdOBJ("dSuggest").style.left = getPos(obj, "Left");
	//将dSuggest的位置设置在$("keyword")的下方
	getIdOBJ("dSuggest").style.top = getPos(obj, "Top") + obj.offsetHeight;
}

//提交之后的事件
function form_submit() {
	//提交该页面中的第一个表单
	document.forms[0].submit();
}

//若点击某项，将某项的值设置到搜索框中并提交表单
function theMouseClick(pos) {
	var key = $("sug").getElementsByTagName("li")[pos].innerHTML;
	//为keyword设置值
	getIdOBJ("keyword").value = key;
	//提交表单
	form_submit();
}