/**
*
**/


$(document).ready(function () {

    $('.nBtn').on('click', function (event) {
        event.preventDefault();
      
            $('.myForm #startDate').val('');
            $('.myForm #endDate').val('');
            $('.myForm #newEventModal').modal();
    });

    $('.table .delBtn').on('click', function (event) {
    	
        event.preventDefault();
        var href = $(this).attr('href');
       
        $('#myModal #delRef').attr('href', href);
        $('#myModal').modal();
    });
});