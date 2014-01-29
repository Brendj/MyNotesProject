/*
 * Copyright (c) 2013. Axetta LLC. All Rights Reserved.
 */

// jQuery plugin to prevent double submission of forms
jQuery.fn.preventDoubleSubmission = function() {
    $(this).on('submit',function(e){
        var $form = $(this);
        if ($form.data('submitted') === true) {
            // Previously submitted - don't submit again
            e.preventDefault();
        } else {
            // Mark it so that the next submit can be ignored
            $form.data('submitted', true);
        }
    });
    // Keep chainability
    return this;
};

function validateTransferForm() {
    var amountField = $('#transferAmountField').val();
    if (amountField.indexOf(',') > 0) {
        amountField.replace(',', '.');
    }
    if (!$.isNumeric(amountField)) {
        $('#errorDiv').html("Размер перевода имеет неправильный числовой формат.");
        return true;
    }
    return false;
}