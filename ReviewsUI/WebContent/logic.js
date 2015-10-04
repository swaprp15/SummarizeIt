/**
 * 
 */

function getReviewsSummary()
{		
	var summaryData;
	
	var productId = document.getElementById('productIdText').value;
	
	$.ajax( {
		type:'Get',
		url:'http://localhost:8080/ReviewsRestService/rest/productsummary/' + productId,
		success:function(data) {
		 summaryData = data;
		 drawChart(summaryData);
		}
		});
}

function drawChart(summaryData)
{
	
	google.load("visualization", "1", {packages:["corechart"], "callback": drawBarChart});
	
	document.getElementById('productName').innerText = summaryData.productName;
	
    function drawBarChart() {
    	
      	var dataTable = new Array();

      	var headers = new Array();
      	headers.push('Feature');
      	headers.push('Positive');
      	headers.push('Negative');
      	
      	dataTable.push(headers);

      	var featureSummaries = summaryData.featureSummaries;


      	for(var i=0; i < featureSummaries.length; i++) {
      		var featureArray = new Array();
      		featureArray.push(featureSummaries[i].featureName);
      		featureArray.push(parseInt(featureSummaries[i].positiveCount));
      		featureArray.push(parseInt(featureSummaries[i].negtiveCount));
      		dataTable.push(featureArray);
      	}

        var data = google.visualization.arrayToDataTable(dataTable);
       
        var options = {
          title: 'Feature Summary',
          vAxis: {title: '',  titleTextStyle: {color: 'red'}},
          height:400,
          width:400,
        };

        var chart = new google.visualization.BarChart(document.getElementById('barchart'));
        chart.draw(data, options);
        
        
      }
}