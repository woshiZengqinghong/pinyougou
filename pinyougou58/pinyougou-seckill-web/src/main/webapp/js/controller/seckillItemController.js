var app = new Vue({
    el:"#app",
    data:{
        timeString:'',
        id:0,
        messageInfo:'',
        goodInfo:{}
    },

    methods:{
        //提交订单
        submitOrder:function () {   //商品Id
            console.log(this.id)
            axios.get('/seckillOrder/submitOrder.shtml?id='+this.id).then(function (response) {
               if (response.data.message=='403'){
                   //没有登录
                   var url = window.location.href;
                   window.location.href="/page/login.shtml?url="+url;
               }else {
                   app.messageInfo=response.data.message;
               }
            })
        },

        //转换时间格式
        convertTimeString:function (time) {
            var allSecond = Math.floor(time/1000);
            //设置年月日
            var days = Math.floor(allSecond/24/3600);
            var hours = Math.floor((allSecond-days*24*3600)/3600);
            var minutes = Math.floor((allSecond-days*24*3600-hours*3600)/60);
            var seconds = Math.floor(allSecond-days*24*3600-hours*3600-minutes*60);

            //设置格式
            if (days<10){
                days = '0'+days;
            }

            if (hours<10){
                hours = '0'+hours;
            }

            if (minutes<10){
                minutes = '0'+minutes;
            }

            if (seconds<10){
                seconds = '0'+seconds;
            }
            return days+'天'+hours+'时'+minutes+'分'+seconds+'秒';
        },

        //倒计时
        calculate:function (time) {
            var clock = window.setInterval(function () {
                time=time-1000;
                app.timeString=app.convertTimeString(time)
                if (time<=0){
                    window.clearInterval(clock);
                }
            },1000)
        },

        /***
         * 获取当前时间与库存
         */
        getGoodsById:function (id) {

            axios.get('/seckillGoods/getGoodsById.shtml',{
                params:{
                    id:id
                }
            }).then(function (response) {

                app.calculate(response.data.time);
                app.goodInfo = response.data;
            })
        },

        /***
         * 查询用户订单状态
         */
        queryStatus:function(){
            var seconds = 0;
               var queryOrder = window.setInterval(function () {
                   seconds+=3;
                    axios.get('/seckillOrder/queryOrderStatus.shtml').then(function (response) {
                        if (response.data.success) {
                            window.clearInterval(queryOrder);
                            //跳转支付页面
                            window.location.href='pay/pay.html';
                        }else {
                            if ('403' == response.data.message) {  //跳转登录页面
                                var url = window.location.href;
                                window.location.href='http://localhost:9111/page/login.shtml?url='+url
                            }else {
                                app.messageInfo = response.data.message+'-----'+seconds;
                            }
                        }
                    })
                },3000)
            }
    },

    created:function () {
        //url获取id
        var obj = this.getUrlParam();
        this.id = 1;

        //获取当前时间与库存
        this.getGoodsById(this.id);
    }
})