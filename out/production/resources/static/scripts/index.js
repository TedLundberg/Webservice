$(document).ready(function(){

    $.ajax({

        url: "/temperatures",
        type: "get",

        success: function(data){
            var chartData = [];
            var chartTimes = [];

            $.each(JSON.parse(data), function(i, val){
                chartData.push(val.value);

                var d = new Date(val.timestamp)

                chartTimes.push(d);
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
              labels: chartTimes,
              series: [
                {
                    data: chartData
                }
              ]
            };

            Chartist.Line('#chartHours', data, options, responsiveOptions);

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