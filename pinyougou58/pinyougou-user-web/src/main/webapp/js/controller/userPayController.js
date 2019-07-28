var app = new Vue({
    el:"#app",
    data:{
        list:[],
        order:{},
        totalMoney:0,
        totalNum:0,
        msg:'',
        payObject:{}
    },
    methods:{
        /*
        * 获取未付款的所有订单
        * */
        getUnpayOrder:function () {
            axios.post('/myOrderList/getUnpayOrder.shtml').then(function (response) {
                //获取数据
                if (response.data == null || response.data == '') {
                    app.msg="无未付款订单"
                }else {
                    app.list = response.data;

                    for (var i = 0; i < app.list.length; i++) {
                        var time = app.list[i].order.createTime;
                        var date = new Date();
                        date.setTime(time);
                        app.list[i].order.createTime = date.toLocaleString();

                    }
                }
            });
        },
        buyThis:function (index) {
            var orderId=this.list[index].order.orderId
            var payment=this.list[index].order.payment
            window.location.href = "pay.html?orderId="+orderId+"&payment="+payment
        },


        /**
         * 创建二维码
         */
        createNative:function (orderId,payment) {
            axios.get('/myOrderList/createNative.shtml',{
                params:{
                    orderId:orderId,
                    payment:payment
                }
            }).then(function (response) {
                if (response.data != null) {
                    app.payObject=response.data;
                    app.payObject.total_fee=response.data.total_fee;

                    //生成二维码
                    var qr = new QRious({
                        element:document.getElementById('qrious'),
                        size:300,
                        level:'H',
                        value:app.payObject.code_url
                    });

                    //二维码存在 查询支付结果
                    if (qr){
                        app.queryPayStatus(app.payObject.out_trade_no)
                    }
                }
            })
        },

        /***
         * 查询支付结果
         * @param out_trade_no
         */
        queryPayStatus:function (out_trade_no) {
            axios.get('/myOrderList/queryPayStatus.shtml',{
                params:{
                    out_trade_no:out_trade_no
                }
            }).then(function (response) {
                if (response.data) {
                    if (response.data.success) {
                        //支付成功
                        window.location.href="paysuccess.html?total_fee="+app.payObject.total_fee;
                    }else {
                        if ("支付超时" == response.data.message) {
                            //支付超时
                            app.createNative()
                        }else {
                            //支付失败
                            window.location.href="payfail.html";
                        }
                    }
                }else {
                    alert("错误")
                }
            })
        }
    },
    created:function () {
        if (window.location.href.indexOf("home-order-pay.html")!=-1) {
            this.getUnpayOrder();
        }
        //判断当前页面
        if (window.location.href.indexOf("pay.html")!=-1){
            var urlParam = this.getUrlParam();
            this.createNative(urlParam.orderId,urlParam.payment)
        }
        if(window.location.href.indexOf("paysuccess.html")!=-1){
            var urlParam = this.getUrlParam();
            this.totalMoney=urlParam.total_fee
        }
    }
})