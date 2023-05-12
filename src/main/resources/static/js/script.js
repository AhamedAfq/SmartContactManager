console.log("This is script file")

const toggleSidebar = () => {
    if($(".sidebar").is(":visible")){

        $(".sidebar").css("display", "none");
        $(".content").css("margin-left", "0%");

    }else{

        $(".sidebar").css("display", "block");
        $(".content").css("margin-left", "20%");
    }
};

const search = () => {
  // console.log("searching...");

  let query = $("#search-input").val();

  if (query == "") {
    $(".search-result").hide();
  } else {
    //search
    console.log(query);

    //sending request to server

    let url = `http://localhost:8282/search/${query}`;

    fetch(url)
      .then((response) => {
        return response.json();
      })
      .then((data) => {
        //data......
        // console.log(data);

        let text = `<div class='list-group'>`;

        data.forEach((contact) => {
          text += `<a href='/user/${contact.contactId}/contact' class='list-group-item list-group-item-action'> ${contact.firstName}  </a>`;
        });

        text += `</div>`;

        $(".search-result").html(text);
        $(".search-result").show();
      });
  }
};


//first request- to server to create order

const paymentStart = () => {
  console.log("payment started ...");
  var amount = $("#payment_field").val();
  console.log(amount);
  if (amount == "" || amount == null) {
    // alert("amount is required !!");
    swal("Failed !!", "amount is required !!", "error");
    return;
  }

  //code...
  // we will use ajax to send request to server to create order- jquery

  $.ajax({
    url: "/user/create_order",
    data: JSON.stringify({ amount: amount, info: "order_request" }), //Got the amount from the paymentStart function
    contentType: "application/json",
    type: "POST",
    dataType: "json",
    success: function (response) {
      //invoked when success
      console.log(response);
      if (response.status == "created") {
        //open payment form
        let options = {
          key: "rzp_test_NHZkMV8FrxkmKB",
          amount: response.amount,
          currency: "INR",
          name: "Contact Manager",
          description: "Donation",
          image:
            "https://static.toiimg.com/thumb/msid-96031819,imgsize-30358,width-400,resizemode-4/96031819.jpg",
          order_id: response.id,
          handler: function (response) {
            console.log(response.razorpay_payment_id);
            console.log(response.razorpay_order_id);
            console.log(response.razorpay_signature);

            updatePaymentOnServer(
              response.razorpay_payment_id,
               response.razorpay_order_id,
                "paid");

            console.log("payment successful !!");
          },
          prefill: {
            name: "",
            email: "",
            contact: "",
          },

          notes: {
            address: "AhamedAfq",
          },
          theme: {
            color: "#3399cc",
          },
        };

        let rzp = new Razorpay(options);

        rzp.on("payment.failed", function (response) {
          console.log(response.error.code);
          console.log(response.error.description);
          console.log(response.error.source);
          console.log(response.error.step);
          console.log(response.error.reason);
          console.log(response.error.metadata.order_id);
          console.log(response.error.metadata.payment_id);
          //alert("Oops payment failed !!");
          swal("Failed !!", "Oops payment failed !!", "error");
        });

        rzp.open();
      }
    },
    error: function (error) {
      //invoked when error
      console.log(error);
      alert("something went wrong !!");
    },
  });
};

function updatePaymentOnServer(payment_id, order_id, status){
  $.ajax({
    url: "/user/update_order ",
    data: JSON.stringify({ payment_id: payment_id, order_id: order_id, status: status }), //Got the amount from the paymentStart function
    contentType: "application/json",
    type: "POST",
    dataType: "json",

    success: function(response){

      // alert("congrates !! Payment successful !!");
      swal("Good job!", "congrates !! Payment successful !!", "success");

    },
    error: function(){

      swal("Failed !!", "Your payment is successful but we coulcn't confirm it. We will contact you shortly as soon as possible", "error");

    }


  })
}