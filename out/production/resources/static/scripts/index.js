$(document).ready(function(){

    $.ajax({

        url: "/temperatures",
        type: "get",

        success: function(data){
            var chartData = [];
            var chartTimes = [];

            //Vill ta fram tiden for det sista datat som lagts in i databasen. Sugig kod. Förmodligen för krånglig!
            var parsedData = JSON.parse(data);
            var tmpLastVal = parsedData[0];
            var tmpDate = new Date(tmpLastVal.timestamp);
            var lastUpdate = tmpDate.getHours() + ":" + tmpDate.getMinutes();

            $.each(JSON.parse(data), function(i, val){
                chartData.push(val.value);

                var d = new Date(val.timestamp)

                chartTimes.push(d.getHours());// + ":" + d.getMinutes());
            });

            var options = {
                seriesBarDistance: 10,
                axisX: {
                    showGrid: false
                },
                height: "245px"
            };

            var responsiveOptions = [
              ['screen and (max-width: 640px)', {
                seriesBarDistance: 5,
                axisX: {
                  labelInterpolationFnc: function (value) {
                    return value[0];
                  }
                }
              }]
            ];

            var data = {
              labels: chartTimes.reverse(),
              series: [
                {
                    data: chartData.reverse()
                }
              ]
            };

            //Update chart
            Chartist.Line('#chartHours', data, options, responsiveOptions);
            //Update "last selected" text
            var myDiv = document.getElementById("indoorTempLastUpdateText");
            myDiv.innerHTML = "Senast uppdaterad " + lastUpdate;
/*
            var ctx = document.getElementById("myChart");
            var myChart = new Chart(ctx, {
                type: 'line',
                data: {
                    labels: chartTimes,
                    datasets: [{
                        label: '# of Votes',
                        data: chartData,
                        borderWidth: 1
                    }]
                },
                options: {
                    scales: {
                        xAxes: [{
                            time: {
                                unit: 'hour'
                            }
                        }]
                    }
                }
            });
            */
        }

    });







});