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
        url: baseURL+'iam/user',
        type: 'POST',
        headers: {
            'Accept': 'application/json',
            'Content-Type': 'application/json',
        },
        data: JSON.stringify(reqObj),
        success: function (data) {
            // Display a success message to the user
            let successMessage = document.createElement('div');
            successMessage.textContent = 'Account created successfully! ';
            successMessage.style.color = 'green';
            successMessage.style.marginTop = '15px';
            document.querySelector('.form-signin').appendChild(successMessage);

            // Redirect the user to the login page after 2 seconds
            setTimeout(function () {
                window.location.href = 'index.html';
            }, 1000);
        },
        error: function (xhr, status, error) {
            // Handle errors from the server
            console.error('Error creating user:', error);
            console.log(xhr.responseText);

            // Display an error message to the user
            let errorMessage = document.createElement('div');
            errorMessage.textContent = 'Error creating account. Please try again.';
            errorMessage.style.color = 'red';
            errorMessage.style.marginTop = '15px';
            document.querySelector('.form-signin').appendChild(errorMessage);
        },

        complete: function() {
            // This block will be executed regardless of success or failure
            console.log('Request completed.');
        }
    });
};