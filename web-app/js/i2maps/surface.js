/**
 * Created with IntelliJ IDEA.
 * User: glenn
 * Date: 22.10.13
 * Time: 07:10
 * To change this template use File | Settings | File Templates.
 */

/* Copyright (c) 2006-2008 MetaCarta, Inc., published under the Clear BSD
 * license.  See http://svn.openlayers.org/trunk/openlayers/license.txt for the
 * full text of the license. */

/**
 * @requires OpenLayers/Layer.js
 * @requires OpenLayers/Tile/Image.js
 */

/**
 * Class: OpenLayers.Layer.Surface
 * Instances of OpenLayers.Layer.Surface are used to display data from a 2d array as a map layer.
 * The data is rendered to a canvas and added to an image layer
 * Create a new image layer with the
 * <OpenLayers.Layer.Image> constructor.  Inherits from <OpenLayers.Layer>.
 */
OpenLayers.Layer.Surface = OpenLayers.Class(OpenLayers.Layer.Image, {

    min_value: 0,
    max_value: 0,
    data: null,
    null_value: -999,

    /**
     * Constructor: OpenLayers.Layer.Image
     * Create a new image layer
     *
     * Parameters:
     * name - {String} A name for the layer.
     * url - {String} Relative or absolute path to the image
     * extent - {<OpenLayers.Bounds>} The extent represented by the image
     * size - {<OpenLayers.Size>} The size (in pixels) of the image
     * options - {Object} Hashtable of extra options to tag onto the layer
     */
    initialize: function(name, options) {
        this.url = $('<canvas></canvas>')[0].toDataURL();
        this.colormap = getColor;
        this.extent = new OpenLayers.Bounds.fromArray([-1120566.8344909, 6875052.0711326, -430187.59514285, 7262740.6785256]);
        this.maxExtent = this.extent;
        this.size = new OpenLayers.Size(10, 10);
        this.aspectRatio = (this.extent.getHeight() / this.size.h) / (this.extent.getWidth() / this.size.w);
        options.isBaseLayer = false;
        this.overlay_layer = "";
        OpenLayers.Layer.prototype.initialize.apply(this, [name, options]);
        selected_point = new OpenLayers.Feature.Vector(new OpenLayers.Geometry.Point(0,0), {value: 0});
        selected_point.surface = this;
        if(this.overlay_layer)
        {
            this.overlay_layer.addFeatures([selected_point]);
            this.overlay_layer.events.on({
                "featureselected": function(e) {
                    if(e.feature.surface)
                    {
                        e.feature.surface.events.triggerEvent("featureselected", e.feature.surface.feature);
                    }
                }
            });
        }
        this.feature = {};
        this.manual_range_set = false;
    },

    pixelToGeo: function(y,x) {
        var lon2px = Math.abs(this.extent.getWidth()) / this.size.w;
        var lat2px = Math.abs(this.extent.getHeight()) / this.size.h;
        var lon = (y * lon2px) - Math.abs(this.extent.left);
        var lat = (((this.size.h-1) - x) * lat2px) + Math.abs(this.extent.bottom);
        return [lat, lon];
    },

    geoToPixel: function(lat, lon) {
        var lon2px = Math.abs(this.extent.getWidth()) / this.size.w;
        var lat2px = Math.abs(this.extent.getHeight()) / this.size.h;
        y = Math.round((lon + Math.abs(this.extent.left)) / lon2px);
        x = (this.size.h-1) - Math.round((lat - Math.abs(this.extent.bottom)) / lat2px);
        return [x, y];
    },
    /**
     * Method: destroy
     * Destroy this layer
     */
    destroy: function() {
        if (this.tile) {
            this.removeTileMonitoringHooks(this.tile);
            this.tile.destroy();
            this.tile = null;
        }
        OpenLayers.Layer.prototype.destroy.apply(this, arguments);
    },

    onmousemove: function(e) {
        var coord = this.getLonLatFromViewPortPx(new OpenLayers.Pixel(e.xy.x, e.xy.y));
        if (coord.lat >= this.extent.bottom && coord.lat <= this.extent.top &&
            coord.lon >= this.extent.left && coord.lon <= this.extent.right)
        {
            px = this.geoToPixel(coord.lat, coord.lon);
            if (this.data != null) {
                val = this.data[px[0]][px[1]] * 1.0;
            } else {
                val = null;
            }
            this.value = val;
            this.renderLegend();
            this.events.triggerEvent("mousemove", {
                'x': coord.lon,
                'y': coord.lat,
                'pixel_x': px[0],
                'pixel_y': px[1],
                'value': val,
                'event': e,
                'value_range': [this.min_value, this.max_value],
                'legend_image' : this.legend_data_url,
            });
        }
    },

    onclick: function(e) {
        if(!this.visibility){
            return;
        }


        if (this.overlay_layer) {
            var f = this.overlay_layer.features[0];
            if (f && f.renderIntent != 'select') {
                f.renderIntent = 'select';
                this.overlay_layer.selectedFeatures.push(f);
                this.overlay_layer.drawFeature(f);
            }
        }

        //     var sx = that.size.w / img.width();
        //     var sy = that.size.h / img.height();
        //     data_x = parseInt((e.clientX - img.offset().left) * sx);
        //     data_y = parseInt((e.clientY - img.offset().top) * sy);
        //     var value = that.data[data_y][data_x];


        var coord = this.getLonLatFromViewPortPx(new OpenLayers.Pixel(e.xy.x, e.xy.y));
        if (coord.lat >= this.extent.bottom && coord.lat <= this.extent.top &&
            coord.lon >= this.extent.left && coord.lon <= this.extent.right)
        {
            px = this.geoToPixel(coord.lat, coord.lon);
            if (this.data != null) {
                val = this.data[px[0]][px[1]] * 1.0;
            } else {
                val = null;
            }
            if (val != null && val > -99) {
                this.feature.x = px[1];
                this.feature.y = px[0];
                this.feature.coord = {'x': coord.lon, 'y': coord.lat};
                this.drawSelectedPoint();
                this.events.triggerEvent("featureselected", this.feature);
            }
        }
    },

    setValueRange: function(min, max){
        this.manual_range_set = true;
        this.max_value = max;
        this.min_value = min;
    },


    setData: function(json) {
        test = json;
        json = json.features[0];
        this.data = eval(json.properties.values);
        var geojson_format = new OpenLayers.Format.GeoJSON();
        geometry = geojson_format.read(json.geometry, "Geometry");
        var redraw = this.extent.equals(geometry.getBounds());
        this.extent = geometry.getBounds();
        this.maxExtent = this.extent;
        this.size = new OpenLayers.Size(this.data[0].length, this.data.length);
        var n = this.null_value;
        flat = this.data.reduce(function(a,b){return a.concat(b)}).filter(function(a){return a != n});
        if(!this.manual_range_set)
        {
            this.max_value = flat.reduce(function(a,b){return Math.max(a,b)});
            this.min_value = flat.reduce(function(a,b){return Math.min(a,b)});
        }
        this.aspectRatio = (this.extent.getHeight() / this.size.h) / (this.extent.getWidth() / this.size.w);
        this.renderData();
        this.tile.imgDiv.src = this.url;
        if(!redraw)
        {
            this.redraw();
        }
        this.setupMouseEvents();
        this.drawSelectedPoint();
        this.feature.attributes = {};
        for(k in json.properties)
        {
            if(k != 'values') this.feature.attributes[k] = json.properties[k];
        }
    },

    addTileMonitoringHooks: function(tile) {
        tile.onLoadStart = function() {
            this.events.triggerEvent("loadstart");
        };
        tile.events.register("loadstart", this, tile.onLoadStart);

        tile.onLoadEnd = function() {
            this.setupEvents();
            this.events.triggerEvent("loadend");
        };
        tile.events.register("loadend", this, tile.onLoadEnd);
        tile.events.register("unload", this, tile.onLoadEnd);
    },

    /**
     * APIMethod: getURL
     * The url we return is always the same (the image itself never changes)
     *     so we can ignore the bounds parameter (it will always be the same,
     *     anyways)
     *
     * Parameters:
     * bounds - {<OpenLayers.Bounds>}
     */
    getURL: function(bounds) {
        return this.url;
    },

    renderData: function() {
        var canvas = $('<canvas></canvas>')[0];
        canvas.width = this.size.w;
        canvas.height = this.size.h;
        if (canvas.getContext){
            var ctx = canvas.getContext('2d');
            var canvasData = ctx.getImageData(0, 0, canvas.width, canvas.height);
        }
        var min_value = this.min_value;
        var max_value = this.max_value;
        for (var x = 0; x < canvasData.width; x++)  {
            for (var y = 0; y < canvasData.height; y++)  {
                // Index of the pixel in the array
                var idx = (x + y * canvas.width) * 4;
                v = this.data[y][x];
                a = 255;
                if(v == this.null_value)
                {
                    v = min_value;
                    a = 0;
                }
                else if(v < min_value)
                {
                    v = min_value;
                    a = 0;
                }
                else if(v > max_value)
                {
                    v = max_value;
                }
                color = this.colormap(v, min_value, max_value);
                canvasData.data[idx + 0] = color[0]; // Red channel
                canvasData.data[idx + 1] = color[1]; // Green channel
                canvasData.data[idx + 2] = color[2]; // Blue channel
                canvasData.data[idx + 3] = a; // Alpha channel
            }
        }
        ctx.putImageData(canvasData, 0, 0);
        this.url = canvas.toDataURL();
    },

    renderLegend: function() {
        if(this.drawing) return;
        this.drawing = true;
        var canvas = $('<canvas></canvas>')[0];
        canvas.width = 1;
        canvas.height = 100;
        var range = this.max_value - this.min_value;
        var s = range / 100;
        var v_y = Math.round((this.value - this.min_value) * (100.0/range))
        var ctx = canvas.getContext('2d');
        var canvasData = ctx.getImageData(0, 0, canvas.width, canvas.height);
        for (var x = 0; x < canvas.width; x++)  {
            for(var y=0; y < canvas.height; y++)
            {
                var v = (y * s) + this.min_value;
                var idx = (x + ((canvas.height - y) * canvas.width)) * 4;
                color = this.colormap(v, this.min_value, this.max_value);
                if(y == v_y) color = [255,0,0];
                canvasData.data[idx + 0] = color[0]; // Red channel
                canvasData.data[idx + 1] = color[1]; // Green channel
                canvasData.data[idx + 2] = color[2]; // Blue channel
                canvasData.data[idx + 3] = 255; // Alpha channel
            }
        }
        ctx.putImageData(canvasData, 0, 0);
        this.legend_data_url = canvas.toDataURL();
        this.drawing = false;
    },

    setupEvents: function() {
    },

    setupMouseEvents: function() {
        var img = $(this.tile.imgDiv);
        var that = this;
        var data_x;
        var data_y;
        var mouse_down = false;
        this.map.events.register('mousemove', this, this.onmousemove);
        this.map.events.register('click', this, this.onclick);
        // $(this.tile.frame).mousemove(function(e){
        //     mouse_down = false;
        //     var sx = that.size.w / img.width();
        //     var sy = that.size.h / img.height();
        //     data_x = parseInt((e.clientX - img.offset().left) * sx);
        //     data_y = parseInt((e.clientY - img.offset().top) * sy);
        //     var value = that.data[data_y][data_x];
        //     that.onmousemove(value);
        //     that.events.triggerEvent("mousemove", that.feature);
        // });
        // $(this.tile.frame).mousedown(function(e){
        //     mouse_down = true;
        // });
        // $(this.tile.frame).mouseup(function(e){
        //     if(mouse_down)
        //     {
        //         that.feature.x = data_x;
        //         that.feature.y = data_y;
        //         that.drawSelectedPoint();
        //         that.events.triggerEvent("featureselected", that.feature);
        //     }
        //     mouse_down = false;
        // });
        this.setupMouseEvents = function(){}; // Events should only be setup once after the first draw
    },

    drawSelectedPoint: function() {
        if(this.feature.x)
        {
            var latlon = this.pixelToGeo(this.feature.x, this.feature.y);
            this.feature.value = this.data[this.feature.y][this.feature.x] * 1.0;
            selected_point.attributes.value = this.feature.value.toFixed(1);
            selected_point.move(new OpenLayers.LonLat(latlon[1], latlon[0]));
        }
    },

    CLASS_NAME: "OpenLayers.Layer.Surface"
});