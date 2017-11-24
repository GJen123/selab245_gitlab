<%@ page language="java" contentType="text/html; charset=BIG5"
	pageEncoding="utf-8"%>
<%@ page
	import="fcu.selab.progedu.conn.Language"%>
<%@ page
	import="fcu.selab.progedu.db.UserDbManager, fcu.selab.progedu.db.ProjectDbManager"%>
<%@ page
	import="fcu.selab.progedu.data.User, fcu.selab.progedu.data.Project"%>
<%@ page import="java.util.*"%>
<%@ page
	import="org.json.JSONArray, org.json.JSONException, org.json.JSONObject"%>

<%
	if (session.getAttribute("username") == null || session.getAttribute("username").toString().equals("")) {
		response.sendRedirect("index.jsp");
	}
	session.putValue("page", "dashboard");
%>

<%@ include file="language.jsp"%>

<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<style type="text/css">
body, html, .container-fluid {
	height: 100%;
}

#allProject {
	margin: 10px 0px 0px 0px;
}

.sidebar {
	background-color: #444;
	color: white;
	margin: -1px;
}

.sidebar a {
	color: white;
}

.sidebar button {
	color: white;
	background: none;
}

#inline {
	margin: 20px;
}

#inline p {
	display: inline;
}

.ovol {
	border-radius: 5px;
	height: 50px;
	font-weight: bold;
	width: 120px;
	color: white;
	text-align: center;
}

.circle {
	border-radius: 30px;
	height: 30px;
	font-weight: bold;
	width: 30px;
	color: white;
	text-align: center;
}

.red {
	background: #e52424;
}

.blue {
	background: #5fa7e8;
}

.gray {
	background: #878787;
}

.orange {
	background: #FF5809;
}

.green {
	background: #32CD32;
}

.gold {
	background: #FFD700;
}

.circle a {
	color: #fff;
}

.highcharts-container {
	text-align: center;
}
.axis path,
.axis line {
  fill: none;
  stroke: #000;
  shape-rendering: crispEdges;
}

.dot {
  stroke: #000;
}
</style>

<link rel="shortcut icon" href="img/favicon.ico" />
<link rel="bookmark" href="img/favicon.ico" />
<script src="//d3js.org/d3.v3.min.js"></script>
<title>ProgEdu</title>
</head>
<body>
	<%
		UserDbManager db = UserDbManager.getInstance();
		ProjectDbManager Pdb = ProjectDbManager.getInstance();

		// db users
		List<User> users = db.listAllUsers();

		// db projects
		List<Project> dbProjects = Pdb.listAllProjects();

	%>
	<%@ include file="header.jsp"%>
	<div class="container-fluid">
		<div class="row">
			<!-- -----sidebar----- -->
			<nav class="col-sm-3 col-md-2 hidden-xs-down bg-faded sidebar">
			<ul class="nav flex-column" style="padding-top: 20px;">
				<li class="nav-item"><font size="4"><a
						href="javascript:;" data-toggle="collapse" data-target="#overview"
						class="nav-link"><i class="fa fa-bars" aria-hidden="true"></i>&nbsp;
							<fmt:message key="dashboard_a_overview" /> <i
							class="fa fa-chevron-down" aria-hidden="true"></i></a></font>
					<ul id="overview" class="collapse" style="list-style: none;">
						<li class="nav-item"><font size="3"><a
								class="nav-link" href="#Student Projects"><i
									class="fa fa-table" aria-hidden="true"></i>&nbsp; <fmt:message
										key="dashboard_li_studentProjects" /></a></font></li>
						<li class="nav-item"><font size="3">
								<button type="button" class="btn btn-default"
									data-toggle="modal" data-target="#exampleModal">
									<i class="fa fa-bar-chart" aria-hidden="true"></i>&nbsp;
									<fmt:message key="dashboard_li_chart" />
								</button>
						</font></li>
					</ul></li>
				<li class="nav-item"><font size="4"><a
						href="javascript:;" data-toggle="collapse" data-target="#student"
						class="nav-link"><i class="fa fa-bars" aria-hidden="true"></i>&nbsp;
							<fmt:message key="dashboard_a_student" /> <i
							class="fa fa-chevron-down" aria-hidden="true"></i></a></font>
					<ul id="student" class="collapse" style="list-style: none;">
						<%
							for (User user : users) {
								String userName = user.getUserName();
								String href = "\"dashStuChoosed.jsp?studentId=" + user.getGitLabId() + "\"";
						%>
						<li class="nav-item"><font size="3"><a
								class="nav-link" href=<%=href%>><i
									class="fa fa-angle-right" aria-hidden="true"></i>&nbsp; <%=userName%></a></font></li>
						<%
							}
						%>
					</ul></li>
			</ul>
			</nav>
			<!-- -----sidebar----- -->
			<main class="col bg-faded py-3 col-md-10">
			<div class="container-fluid col-md-12" style="margin-top: 20px;">
				<h1 style="margin-top: 30px; margin-bottom: 20px;">
					<fmt:message key="dashboard_a_overview" />
				</h1>
				<!-- ---------------------------- Student Project ------------------------------- -->
				<div class="card col-md-12" style="padding:0;">
					<h4 id="Student Projects" class="card-header col-md-12">
						<i class="fa fa-table" aria-hidden="true"></i>&nbsp;
						<fmt:message key="dashboard_li_studentProjects" />
					</h4>
					<div class="card-block">
						<ul class="nav nav-tabs" role="tablist">
							<li class="nav-item"><a class="nav-link active"
								data-toggle="tab" href="#chart1" role="tab">Chart1</a></li>
							<li class="nav-item"><a class="nav-link" data-toggle="tab"
								href="#chart2" role="tab">Chart2</a></li>
						</ul>
						<!-- Tab panes -->
						<div class="tab-content text-center" style="margin-top: 10px">
							<div class="tab-pane active col-md-12" id="chart1" role="tabpanel">
								<div class="col-md-12" id="chart1Demo"
									style="min-width: 310px; max-width: 1200px; height: 400px; margin: 0 auto"></div>
							</div>
							<div class="tab-pane col-md-12" id="chart2" role="tabpanel">
								<div class="col-md-12" id="chart2Demo"
									style="min-width: 310px; max-width: 1200px; height: 400px; margin: 0 auto"></div>
							</div>
						</div>
					</div>
					<script>
						var chart1Array = [];
						var projectNames = <%=Pdb.listAllProjectNames()%>
					</script>
					<%
						String[] colors = {"blue", "red", "orange", "gray"};
						for (String color : colors) {
					%>
							<script type="text/javascript">
							var color = <%="'" + color + "'"%>;
							$.ajax({
								url : 'webapi/commits/color',
								type : 'GET',
								data: {
									"color" : color
								}, 
								async : false,
								cache : true,
								contentType: 'application/json; charset=UTF-8',
								success : function(responseText) {
									var str = JSON.stringify(responseText);
									var obj = JSON.parse(str);
									chart1Array.push(obj);
								}, 
								error : function(responseText,A,B) {
									console.log(responseText,A,B);
								}
							});
							</script>
					<%
						}
					%>
					<script>
						var chart2Array = [];
						$.ajax({
							url : 'webapi/commits/count',
							type : 'GET',
							async : false,
							cache : true,
							contentType: 'application/json; charset=UTF-8',
							success : function(responseText) {
								var str = JSON.stringify(responseText);
								var obj = JSON.parse(str);
								chart2Array.push(obj);
							}, 
							error : function(responseText,A,B) {
								console.log(responseText,A,B);
							}
						});
					</script>
					<%
						for (String color : colors) {
							if(color.equals("gray")) {
								continue;
							}
					%>
							<script type="text/javascript">
							var color = <%="'" + color + "'"%>;
							$.ajax({
								url : 'webapi/commits/record/color',
								type : 'GET',
								data: {
									"color" : color
								}, 
								async : false,
								cache : true,
								contentType: 'application/json; charset=UTF-8',
								success : function(responseText) {
									var str = JSON.stringify(responseText);
									var obj = JSON.parse(str);
									chart2Array.push(obj);
								}, 
								error : function(responseText,A,B) {
									console.log(responseText,A,B);
								}
							});
							</script>
					<%
						}
					%>
				</div>
			</div>
			</main>
		</div>
	</div>
</body>
<!-- set Highchart colors -->
<script>
Highcharts.setOptions({
	 colors: ['#5fa7e8', '#e52424', '#FF5809', '#878787']
	})
</script>

<script>
Highcharts.chart('chart1Demo', {
    chart: {
        type: 'column'
    },
    title: {
        text: '各作業建置結果統計'
    },
    subtitle: {
        text: ''
    },
    xAxis: {
        categories: projectNames,
        crosshair: true
    },
    yAxis: {
        min: 0,
        title: {
            text: '個數'
        }
    },
    tooltip: {
        headerFormat: '<span style="font-size:10px">{point.key}</span><table>',
        pointFormat: '<tr><td style="color:{series.color};padding:0">{series.name}: </td>' +
            '<td style="padding:0"><b>{point.y}</b></td></tr>',
        footerFormat: '</table>',
        shared: true,
        useHTML: true
    },
    plotOptions: {
        column: {
            pointPadding: 0.2,
            borderWidth: 0
        }
    },
    series: chart1Array
});
</script>

<!-- set Highchart colors -->
<script>
Highcharts.setOptions({
	 colors: ['#878787', '#5fa7e8', '#e52424', '#FF5809']
	})
</script>

<script>
Highcharts.chart('chart2Demo', {
    chart: {
        zoomType: 'xy'
    },
    title: {
        text: '各作業上傳次數及建置結果統計'
    },
    subtitle: {
        text: ''
    },
    xAxis: [{
        categories: projectNames,
        crosshair: true
    }],
    yAxis: [{ // Primary yAxis
        labels: {
            format: '',
            style: {
                color: Highcharts.getOptions().colors[0]
            }
        },
        title: {
            text: '次數',
            style: {
                color: Highcharts.getOptions().colors[0]
            }
        }
    }, { // Secondary yAxis blue
        title: {
            text: '個數',
            style: {
                color: Highcharts.getOptions().colors[0]
            }
        },
        labels: {
            format: '',
            style: {
                color: Highcharts.getOptions().colors[0]
            }
        },
        opposite: true
    }],
    tooltip: {
        shared: true
    },
    legend: {
        layout: 'vertical',
        align: 'left',
        x: 120,
        verticalAlign: 'top',
        y: 100,
        floating: true,
        backgroundColor: (Highcharts.theme && Highcharts.theme.legendBackgroundColor) || '#FFFFFF'
    },
    series: chart2Array
});
</script>

</html>