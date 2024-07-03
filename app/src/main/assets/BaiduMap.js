var map;
var points = []; // 存储轨迹点的数组
var polyline; // 用于绘制轨迹线的对象
var mapIsBeingDragged = false; // 追踪地图拖动状态
var marker;

function initMap() {
    map = new BMap.Map("map"); // 创建百度地图实例
    var point = new BMap.Point(116.404, 39.915); // 创建一个初始点坐标
    map.centerAndZoom(point, 15); // 初始化地图，设置中心点和缩放级别

    // 添加地图控件
    map.addControl(new BMap.NavigationControl());
    map.addControl(new BMap.ScaleControl());
    map.addControl(new BMap.OverviewMapControl());
    map.addControl(new BMap.MapTypeControl());

    // 添加地图拖动事件监听器
    map.addEventListener("dragstart", function() {
        mapIsBeingDragged = true;
    });

    map.addEventListener("dragend", function() {
        mapIsBeingDragged = false;
    });
}

// 调用 initMap 函数初始化地图
document.addEventListener('DOMContentLoaded', initMap);

// 创建 WebSocket 连接和自动重连逻辑
var socket = null;
var socketUrl = 'ws://8.137.81.229:8880'; // 替换为你的 WebSocket URL

function connectWebSocket() {
    socket = new WebSocket(socketUrl);

    socket.onopen = function(event) {
        console.log('WebSocket is connected.');
        // 成功连接后可以向服务器发送消息
        socket.send('Hello Server!');
    };

    socket.onmessage = function(event) {
        console.log('Received message from server: ' + event.data);

        // 解析 JSON 数据
        var data = JSON.parse(event.data);

        // 提取经度和纬度
        var longitude = data.lon;
        var latitude = data.lat;

        // 更新经度和纬度文本元素
        var lonElement = document.querySelector('.text-lon');
        var latElement = document.querySelector('.text-lat');
        lonElement.textContent = '经度: ' + longitude.toFixed(10);
        latElement.textContent = '纬度: ' + latitude.toFixed(10);

        // 转换为 BD-09 坐标
        var bd09 = wgs84ToBd09(data.lon, data.lat);

        // 添加新的点到轨迹点数组中，但只有当经纬度大于阈值时才添加
        if (Math.abs(data.lon) > 0.0065 && Math.abs(data.lat) > 0.0065) {
            var newPoint = new BMap.Point(bd09[0], bd09[1]);
            points.push(newPoint);

            // 删除之前的轨迹线（如果存在），但只有在地图没有被拖动时才进行中心移动和轨迹线绘制
            if (!mapIsBeingDragged) {
                if (polyline) {
                    map.removeOverlay(polyline);
                }
                // 清除上一次的标记
                if (marker) {
                    map.removeOverlay(marker);
                    marker = null;
                }

                // 创建新的轨迹线
                polyline = new BMap.Polyline(points, {strokeColor:"red", strokeWeight:6, strokeOpacity:1});
                map.addOverlay(polyline);

                // 创建标记
                marker = new BMap.Marker(newPoint);
                marker.setIcon(new BMap.Icon('img/huaji.png', new BMap.Size(16, 45)));

                // 添加标记到地图
                map.addOverlay(marker);

                // 将地图中心移动到最新点
                map.panTo(newPoint);
            }
        }
    };

    socket.onclose = function(event) {
        console.log('WebSocket is closed. Reconnecting...');
        alert('WebSocket连接已断开，正在尝试重新连接...');
        setTimeout(connectWebSocket, 100); // 2秒后尝试重新连接
    };

    socket.onerror = function(error) {
        console.log('WebSocket error: ' + error);
        // 在这里处理错误
    };
}

// 初始连接
connectWebSocket();

function wgs84ToGcj02(lon, lat) {
    var pi = 3.1415926535897932384626;
    var a = 6378245.0;
    var ee = 0.00669342162296594323;

    function transformLat(x, y) {
        var ret = -100.0 + 2.0 * x + 3.0 * y + 0.2 * y * y + 0.1 * x * y + 0.2 * Math.sqrt(Math.abs(x));
        ret += (20.0 * Math.sin(6.0 * x * pi) + 20.0 * Math.sin(2.0 * x * pi)) * 2.0 / 3.0;
        ret += (20.0 * Math.sin(y * pi) + 40.0 * Math.sin(y / 3.0 * pi)) * 2.0 / 3.0;
        ret += (160.0 * Math.sin(y / 12.0 * pi) + 320 * Math.sin(y * pi / 30.0)) * 2.0 / 3.0;
        return ret;
    }

    function transformLon(x, y) {
        var ret = 300.0 + x + 2.0 * y + 0.1 * x * x + 0.1 * x * y + 0.1 * Math.sqrt(Math.abs(x));
        ret += (20.0 * Math.sin(6.0 * x * pi) + 20.0 * Math.sin(2.0 * x * pi)) * 2.0 / 3.0;
        ret += (20.0 * Math.sin(x * pi) + 40.0 * Math.sin(x / 3.0 * pi)) * 2.0 / 3.0;
        ret += (150.0 * Math.sin(x / 12.0 * pi) + 300.0 * Math.sin(x / 30.0 * pi)) * 2.0 / 3.0;
        return ret;
    }

    var dLat = transformLat(lon - 105.0, lat - 35.0);
    var dLon = transformLon(lon - 105.0, lat - 35.0);
    var radLat = lat / 180.0 * pi;
    var magic = Math.sin(radLat);
    magic = 1 - ee * magic * magic;
    var sqrtMagic = Math.sqrt(magic);
    dLat = (dLat * 180.0) / ((a * (1 - ee)) / (magic * sqrtMagic) * pi);
    dLon = (dLon * 180.0) / (a / sqrtMagic * Math.cos(radLat) * pi);
    var mgLat = lat + dLat;
    var mgLon = lon + dLon;

    return [mgLon, mgLat];
}

function gcj02ToBd09(lon, lat) {
    var x_pi = 3.14159265358979324 * 3000.0 / 180.0;
    var z = Math.sqrt(lon * lon + lat * lat) + 0.00002 * Math.sin(lat * x_pi);
    var theta = Math.atan2(lat, lon) + 0.000003 * Math.cos(lon * x_pi);
    var bd_lon = z * Math.cos(theta) + 0.0065;
    var bd_lat = z * Math.sin(theta) + 0.006;

    return [bd_lon, bd_lat];
}

function wgs84ToBd09(lon, lat) {
    var gcj02 = wgs84ToGcj02(lon, lat);
    return gcj02ToBd09(gcj02[0], gcj02[1]);
}
