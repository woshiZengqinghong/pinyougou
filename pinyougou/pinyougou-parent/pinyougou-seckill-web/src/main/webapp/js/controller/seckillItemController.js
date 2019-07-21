var app = new Vue({
    el: "#app",
    data: {
        pages: 15,
        pageNo: 1,
        list: [],
        entity: {},
        ids: [],
        seckillId:0,
        goodsInfo:{},
        messageInfo:'',
        timeString: '',
        searchEntity: {},
    },
    methods: {
        //查询订单状态 当点击立即抢购之后执行
        queryStatus:function () {
            var count = 0;
            //三秒执行一次
            var queryOrder = window.setInterval(function(){
                count+=3;
                axios.get("/seckillOrder/queryOrderStatus.shtml").then(function (response) {

                    if (response.data.success) {
                        //跳转到支付页面
                        window.clearInterval(queryOrder);
                        alert("跳转到支付页面")

                    }else{
                        if (response.data.message == '403') {
                            //要登陆
                        }else{
                            //不需要登陆需要提示
                            app.messageInfo=response.data.message+"....."+count;
                        }
                    }
                })
            },3000);
        },
        /**
         *
         * @param alltime 为 时间的毫秒数。
         * @returns {string}
         */
        convertTimeString: function (alltime) {
            var allsecond = Math.floor(alltime / 1000);//毫秒数转成 秒数。
            var days = Math.floor(allsecond / (60 * 60 * 24));//天数
            var hours = Math.floor((allsecond - days * 60 * 60 * 24) / (60 * 60));//小数数
            var minutes = Math.floor((allsecond - days * 60 * 60 * 24 - hours * 60 * 60) / 60);//分钟数
            var seconds = allsecond - days * 60 * 60 * 24 - hours * 60 * 60 - minutes * 60; //秒数
            if (days > 0) {
                days = days + "天 ";
            }
            if (hours < 10) {
                hours = "0" + hours;
            }
            if (minutes < 10) {
                minutes = "0" + minutes;
            }
            if (seconds < 10) {
                seconds = "0" + seconds;
            }
            if(days<1){

                return hours + ":" + minutes + ":" + seconds;
            }else{
                return days + hours + ":" + minutes + ":" + seconds;

            }
        },
        //倒计时
        calculate: function (alltime) {

            let clock = window.setInterval(function () {
                alltime = alltime - 1000;
                //反复被执行的函数
                app.timeString = app.convertTimeString(alltime);
                if (alltime <= 0) {
                    //取消
                    window.clearInterval(clock);
                }
            }, 1000);//相隔1000执行一次。
        } ,
        submitOrder:function () {
            axios.get("/seckillOrder/submitOrder/"+this.seckillId+".shtml").then(function (response) {
                if (response.data.success) {
                    app.messageInfo=response.data.message;
                }else{
                    if (response.data.message == "403") {
                        //说明没有登陆
                        var url = window.location.href;
                        window.location.href="/page/login.shtml?url="+url;
                    }else{
                        app.messageInfo=response.data.message;
                    }
                }
            }).catch(function (error) {

            })
        },
        getGoodsById:function (id) {
            axios("/seckillGoods/getGoodsById.shtml",{
                params:{
                    id:id,
                }
            }).then(function (response) {
                console.log(response.data);
                app.goodsInfo=response.data;
                app.calculate(response.data.time);
                console.log(app.goodsInfo);
            })
        },
    },

    created: function () {
        //开始执行获取参数
        var urlParam = this.getUrlParam("id");
        //获取秒杀商品的ID
        this.seckillId=urlParam.id;
        //获取商品的信息
        this.getGoodsById(this.seckillId);
    }
});