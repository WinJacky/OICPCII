<%--
  Created by IntelliJ IDEA.
  User: wuchangjiao
  Date: 2019/9/14
  Time: 11:00
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; Charset=gb2312">
    <meta http-equiv="Content-Language" content="zh-CN">
    <meta name="viewport" content="width=device-width, initial-scale=1, user-scalable=no"/>
    <title>OICPCII</title>
    <link rel="stylesheet" href="static/plug/layui/css/layui.css">
    <%--网页图标--%>
    <link rel="shortcut icon" href="static/images/COIPIB.png" type="image/x-icon">
    <!--font-awesome-->
    <link href="static/plug/font-awesome/css/font-awesome.min.css" rel="stylesheet"/>
    <!--全局样式表-->
    <link href="static/css/global.css" rel="stylesheet"/>
    <%--分页样式表--%>
    <link href="static/css/pageInfo/page.css" rel="stylesheet">
    <%--index页样式--%>
    <link href="static/css/index.css" rel="stylesheet">
    <!-- 最新版本的 Bootstrap 核心 CSS 文件 -->
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap@3.3.7/dist/css/bootstrap.min.css" integrity="sha384-BVYiiSIFeK1dGmJRAkycuHAHRg32OmUcww7on3RYdg4Va+PmSTsz/K68vbdEjh4u" crossorigin="anonymous">

    <!-- 引入顺序也要注意下,bootstrap.js 依赖于jQuery.js -->
    <script src='static/js/jquery/jquery.min.js'></script>

    <!-- 最新的 Bootstrap 核心 JavaScript 文件 -->
    <script src="https://cdn.jsdelivr.net/npm/bootstrap@3.3.7/dist/js/bootstrap.min.js" integrity="sha384-Tc5IQib027qvyjSMfHjOMaLkfuWVxZxUPnCJA7l2mCWNIpG9mGCD8wGNIcPD7Txa" crossorigin="anonymous"></script>

    <style>
        .bias1{
            border:1px solid;
            margin-top: 23px;
            -webkit-transform: rotate(15deg);/*Safari 4+,Google Chrome 1+  */
            -moz-transform: rotate(15deg);/*Firefox 3.5+*/
            filter: progid:DXImageTransform.Microsoft.BasicImage(Rotation=0.15);
        }
        .bias2{
            border:1px solid;
            margin-top: 23px;
            -webkit-transform: rotate(20deg);/*Safari 4+,Google Chrome 1+  */
            -moz-transform: rotate(20deg);/*Firefox 3.5+*/
            filter: progid:DXImageTransform.Microsoft.BasicImage(Rotation=0.2);
        }
    </style>
</head>
<body class="layui-layout-body">

<div class="layui-layout layui-layout-admin">
    <div class="layui-header position: absolute;">
        <a href="${ctx}/index.jsp">
            <div class="layui-logo doc-logo" style="font-standard: bold">OICPCII</div>
        </a>
        <ul class="layui-nav layui-layout-left small-head-nav-left">
            <li class="layui-nav-item"><a href="javascript:;"></a></li>
        </ul>
        <!-- 头部区域（可配合layui已有的水平导航） -->
        <ul class="layui-nav layui-layout-left head-nav-left">

            <li class="dropdown pull-right layui-nav-item">
                <a href="#" onClick="javascript:parameter();return false;">参数预设</a>
            </li>
            <!--禁止删除-->
            <li class="layui-nav-item" id="adminMenu">

            </li>
        </ul>
        <ul class="layui-nav layui-layout-right head-nav-right">
            <button type="button" class="btn btn-primary btn-lg" style="font-size:12px;margin-right:20px" id="userInfoButton">
                <span class="glyphicon glyphicon-user"></span>
            </button>
        </ul>

        <a class="small-doc-navicon" href="javascript:;" onclick="showLeftNav();">
            <i class="fa fa-navicon"></i>
        </a>
    </div>

    <div class="blog-main">
        <div style="margin-left: 20px;margin-right: 50px;" id="parameterDiv">
            <table class="table table-hover table-bordered" style="text-align:center;margin:20px;">
                <tr><td rowspan="2" style="padding: 0px" width="180px;"><img src="static/images/table1.png"></td><td>经贸合作区</td><td>加工制造园区</td><td>商贸物流园区</td><td>科技研发园区</td><td>其他园区</td><td rowspan="2">权重</td></tr>
                <tr><td>标准(%)</td><td>标准(%)</td><td>标准(%)</td><td>标准(%)</td><td>标准(%)</td></tr>
                <tr><td>居住用地</td><td id="live0"></td><td id="live1"></td><td id="live2"></td><td id="live3"></td><td id="live4"></td><td id="live5"></td></tr>
                <tr><td>生活服务设施用地</td><td id="facility0"></td><td id="facility1"></td><td id="facility2"></td><td id="facility3"></td><td id="facility4"></td><td id="facility5"></td></tr>
                <tr><td>工业仓储用地</td><td id="industry0"></td><td id="industry1"></td><td id="industry2"></td><td id="industry3"></td><td id="industry4"></td><td id="industry5"></td></tr>
                <tr><td>生产配套设施用地</td><td id="produce0"></td><td id="produce1"></td><td id="produce2"></td><td id="produce3"></td><td id="produce4"></td><td id="produce5"></td></tr>
                <tr><td>道路与交通设施用地</td><td id="road0"></td><td id="road1"></td><td id="road2"></td><td id="road3"></td><td id="road4"></td><td id="road5"></td></tr>
                <tr><td>绿化用地</td><td id="green0"></td><td id="green1"></td><td id="green2"></td><td id="green3"></td><td id="green4"></td><td id="green5"></td></tr>
            </table>
            <div style="text-align: center;margin: 60px;position: relative">
                <input type="file" id="inputFile" class="form-control" name="inputFile" style="width: 80%;float: left">
                <button class="layui-btn" lay-submit="" lay-filter="formSearch" onclick="updateData()" id="" style="float: left">上传文件 </button>
                <br><br><div style="text-align: left">说明：仅管理员登陆后可通过上传文件修改参数。</div>
            </div>
        </div>
    </div>
</div>

</body>
<script>
    //加载数据
    $(function () {
        $.ajax({
            url:'${ctx}/admin/showWS',	//后台接收数据地址
            data:"",
            type: "POST",
            dataType: "json",
            cache: false,			//上传文件无需缓存
            processData: false,		//用于对data参数进行序列化处理 这里必须false
            contentType: false,
            success:function(result){
                var data=eval('('+result+')');

                var j=0;
                for(var i=0;i<6;i++){
                    document.getElementById('live'+j).innerText=data[i]['occupancy'];
                    document.getElementById('facility'+j).innerText=data[i]['infrastructure'];
                    document.getElementById('industry'+j).innerText=data[i]['depository'];
                    document.getElementById('produce'+j).innerText=data[i]['production'];
                    document.getElementById('road'+j).innerText=data[i]['traffic'];
                    document.getElementById('green'+j).innerText=data[i]['green'];
                    j++;
                }
            },
            failure: function (data) {
                alert(data+"文件上传失败！");
            }
        })
        var name = "<%=session.getAttribute("name")%>";
        var ticket = "<%=session.getAttribute("ticket")%>";

        if (name != null && ticket != null && name != "null" && ticket != "null") {
            var html = "";
            html = html + '<a href="${ctx}/userInfo.jsp;" style="color:white;">欢迎' + name + '</a>';
            $("#userInfoButton").html(html);
        }
    })

    //更新数据
    function updateData(){
        var formData = new FormData();
        formData.append('file', $('#inputFile')[0].files[0]); // 固定格式

        $.ajax({
            url:'${ctx}/admin/updateWS',	//后台接收数据地址
            data:formData,
            type: "POST",
            dataType: "json",
            cache: false,			//上传文件无需缓存
            processData: false,		//用于对data参数进行序列化处理 这里必须false
            contentType: false,
            success: function(result){
                if(result === "success"){
                    alert("默认数据修改成功！");
                    //刷新界面
                    history.go(0);
                }else if(result === "normal user"){
                    alert("非管理员用户无修改权限！");
                }else if(result === "empty file"){
                    alert("管理员输入的文件为空！");
                }else if(result === "unknown error"){
                    alert("发生未知错误！");
                }
            },
            failure: function (data) {
                alert(data+"\n文件传输出错！");
            }
        })
    }

</script>
</html>
