const baseURL = window.location.protocol + "//" + window.location.hostname + ":8080/"

document.getElementById("signup").onclick = function () {
    // Get the values from the input fields
    var userName = $('#inputuserName').val();
    var mail = $('#inputEmail').val();
    var password = $('#inputPassword').val();

    // Validate the email (you can add more sophisticated validation if needed)
    if (!mail) {
        console.error('Email cannot be null.');
        return; // Do not proceed with the signup if email is null
    }

    // Create the request object
    let reqObj = {"mail": mail, "userName":userName, "password": password,"permissionLevel":1};

    // Perform the AJAX request
    $.ajax({
        url: baseURL+'api/user',
        type: 'POST',
        headers: {
            'Accept': 'application/json',
            'Content-Type': 'application/json',
        },
        data: JSON.stringify(reqObj),
        success: function(data) {
            // Handle the success response from the server if needed
            console.log('User created successfully.');
        },
        error: function(xhr, status, error) {
            // Handle errors from the server
            console.error('Error creating user:', error);
            console.log(xhr.responseText);
        },
        complete: function() {
            // This block will be executed regardless of success or failure
            console.log('Request completed.');
        }
    });
};