var app = new Vue({
    el:"#app",
    data:{
        totalMoney:0,
        payObject:{}
    },
    methods:{
        /**
         * 创建二维码
         */
        createNative:function () {
            axios.get('/pay/createNative.shtml').then(function (response) {
                if (response.data != null) {
                    app.payObject=response.data;
                    app.payObject.total_fee=response.data.total_fee/100;

                    //生成二维码
                    var qr = new QRious({
                        element:document.getElementById('qrious'),
                        size:250,
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
            axios.get('/pay/queryPayStatus.shtml',{
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
        //判断当前页面
        if (window.location.href.indexOf("pay.html")!=-1){
            this.createNative()
        }
        if(window.location.href.indexOf("paysuccess.html")!=-1){
            var urlParam = this.getUrlParam();
            this.totalMoney=urlParam.total_fee
        }
    }
})