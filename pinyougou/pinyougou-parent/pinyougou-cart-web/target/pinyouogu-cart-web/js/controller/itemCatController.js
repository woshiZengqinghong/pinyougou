var app = new Vue({
    el: "#app",
    data: {
        cartList: [],
        totalMoney:0,//总金额
        totalNum:0//总数量
    },
    methods: {
        findCartList:function () {
            axios.get("/cart/findCartList.shtml").then(function (response) {
                //获取购物车列表数据
                app.cartList = response.data;
                app.totalMoney=0;
                app.totalNum=0;
                let cartListAll=response.data;

                for (let i = 0; i < cartListAll.length; i++) {
                    var cart = cartListAll[i];
                    for (let j = 0; j < cart.orderItemList.length; j++) {
                        app.totalNum+=cart.orderItemList[i].num;
                        app.totalMoney+=cart.orderItemList[i].totalFee;
                    }
                }
            }).catch(function (error) {

            })
        },
        addGoodsToCartList:function (itemId, num) {
            axios.post("/cart/addGoodsToCartList.shtml",{
                params:{
                    itemId:itemId,
                    num:num
                }
            }).then(function (response) {
                if (response.data.success) {
                    //添加成功
                    app.findCartList();
                }else{
                    //添加失败
                    alert(response.data.message);
                }
            });
        },



    },
    //钩子函数 初始化了事件和
    created: function () {
        this.findCartList();
    }

});
