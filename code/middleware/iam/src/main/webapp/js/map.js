window.randomScalingFactor = function () {
    return (Math.random() > 0.5 ? 1.0 : -1.0) * Math.round(Math.random() * 100);
};



$(document).ready(function () {
    var lyr_osm = new ol.layer.Tile({title: 'OSM',type: 'base',visible: true,source: new ol.source.OSM()});
    var mapView = new ol.View({
        projection: "EPSG:4326",
        center: [ 10.1874518,36.8917497],
        zoom: 9,
      });
var layersList = [lyr_osm];
var style = new ol.style.Style({
    fill: new ol.style.Fill({ color: "rgba(255, 100, 50, 0.3)" }),
    stroke: new ol.style.Stroke({ width: 2, color: "rgba(255, 100, 50, 0.8)" }),
    image: new ol.style.Icon({
        src: 'https://cdn-icons-png.flaticon.com/512/6395/6395271.png',
        scale: 0.1
    }),
  });
var map = new ol.Map({target: 'map',layers: layersList,view: mapView});
$.ajax({
    url: 'https://localhost:8080/smartirrigation-1.0-SNAPSHOT/api/sensor',
    type: 'GET',
    headers: {
        'Accept': 'application/json',
        'Content-Type': 'application/json',
        'Authorization': Authorizationheader
    },
    success: function (data) {
        console.log(data)
        for (let i=0;i<data.length;i++){
            var coordinates=[data[i].longitude,data[i].latitude];
            console.log(coordinates);
            var point = new ol.geom.Point(coordinates);
            
            var pointFeature = new ol.Feature(point);
            var vectorSource = new ol.source.Vector({ projection: "EPSG:4326" });
            var vectorLayer = new ol.layer.Vector({ source: vectorSource, style: style });
            var pointFeature = new ol.Feature(point);
            vectorSource.addFeature(pointFeature);
            map.addLayer(vectorLayer);
      
          }
    }
})

document.getElementById("button zoom").onclick = function () {
    map.getView().fit(map.getView().calculateExtent());
    map.getView().setZoom(0);
}
document.getElementById("button_pos").onclick = function () {
    var geolocation = new ol.Geolocation({
        projection: map.getView().getProjection(),
        tracking: true,
      });
      
      
      var marker = new ol.Overlay({
        element: document.getElementById("location"),
        positioning: "center-center",
      });
      map.addOverlay(marker);
      geolocation.on("change:position", function () {
        map.getView().setCenter(geolocation.getPosition());
        map.getView().setZoom(15);
        marker.setPosition(geolocation.getPosition()
        );
        var style = new ol.style.Style({
            fill: new ol.style.Fill({ color: "rgba(255, 100, 50, 0.3)" }),
            stroke: new ol.style.Stroke({ width: 2, color: "rgba(255, 100, 50, 0.8)" }),
            image: new ol.style.Icon({
                src: 'https://cdn3.iconfinder.com/data/icons/maps-and-navigation-7/65/68-512.png',
                scale: 0.1
            }),
          });
        var point = new ol.geom.Point(geolocation.getPosition());
            
            var pointFeature = new ol.Feature(point);
            var vectorSource = new ol.source.Vector({ projection: "EPSG:4326" });
            var vectorLayer = new ol.layer.Vector({ source: vectorSource, style: style });
            var pointFeature = new ol.Feature(point);
            vectorSource.addFeature(pointFeature);
            map.addLayer(vectorLayer);
        
      });
}

    

});

var accesstoken = localStorage.getItem("accesstoken")
var mail = localStorage.getItem("mail")
var Authorizationheader = "Bearer " + accesstoken
console.log(accesstoken)
