var app = new Vue({
    el: "#app",
    data: {
        cartList: [],
        addressList:[],
        address:{},
        order:{paymentType:'1'},
        totalMoney:0,//总金额
        totalNum:0//总数量
    },
    methods: {
        submitOrder:function () {
            //设置值
            this.$set(this.order,"receiverAreaName",this.address.address);
            this.$set(this.order,"receiverMobile",this.address.mobile);
            this.$set(this.order,"receiver",this.address.contact);
            axios.post("/order/add.shtml",this.order).then(function (response) {
                if(response.data.success){
                    //跳转到支付页面
                    window.location.href="pay.html";
                }else{
                    alert(response.data.message);
                }
            })
        },
        selectType:function (type) {
            console.log(type);
            this.$set(this.order,'paymentType',type);
        },
        selectAddress:function (address) {
            this.address=address;

        },
        isSelectedAddress:function(address){
            if (address == this.address) {
                return true;
            }else{
                return false;
            }
        },
        findAddressList:function () {
            axios("/address/findAddressListByUserId.shtml").then(function (response) {
                app.addressList = response.data;
                for (let i = 0; i < app.addressList.length; i++) {
                    if (app.addressList[i].isDefault == '1') {
                        app.address=app.addressList[i];
                        break;
                    }
                }
            })

        },
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
            axios.get("/cart/addGoodsToCartList.shtml",{
                params:{
                    itemId:itemId,
                    num:num
                },
                withCredentials:true,
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
        //判断如果是getOrderInfo.html的时候才加载
        this.findAddressList();
    }

});
