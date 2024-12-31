const baseURL = window.location.protocol + "//" + window.location.hostname + ":8080/"
function generateRandomString() {
    var array = new Uint32Array(28);
    window.crypto.getRandomValues(array);
    return Array.from(array, dec => ('0' + dec.toString(16)).substr(-2)).join('');
}

// Calculate the SHA256 hash of the input text. 
// Returns a promise that resolves to an ArrayBuffer
function sha256(plain) {
    const encoder = new TextEncoder();
    const data = encoder.encode(plain);
    return window.crypto.subtle.digest('SHA-256', data);
}

// Base64-urlencodes the input string
function base64urlencode(str) {
    // Convert the ArrayBuffer to string using Uint8 array to conver to what btoa accepts.
    // btoa accepts chars only within ascii 0-255 and base64 encodes them.
    // Then convert the base64 encoded to base64url encoded
    //   (replace + with -, replace / with _, trim trailing =)
    return btoa(String.fromCharCode.apply(null, new Uint8Array(str)))
        .replace(/\+/g, '-').replace(/\//g, '_').replace(/=+$/, '');
}

// Return the base64-urlencoded sha256 hash for the PKCE challenge
async function pkceChallengeFromVerifier(v) {
    hashed = await sha256(v);
    return base64urlencode(hashed);
}
function utf8_to_b64(str) {
    return window.btoa(encodeURIComponent(str).replace(/%([0-9A-F]{2})/g, function(match, p1) {
        return String.fromCharCode(parseInt(p1, 16));
    }));
}
window.onload= start()
async function start(){
var state = generateRandomString();
var code_verifier = generateRandomString();
var code_challenge = await pkceChallengeFromVerifier(code_verifier);
console.log(state)
console.log(code_verifier)
localStorage.setItem("codeverif", code_verifier);
console.log(code_challenge)
var step=utf8_to_b64(state+"#"+code_challenge)
// console.log(state+"#"+code_challenge)
// console.log(step)
// console.log(utf8_to_b64('4068d1c7-7945-47f2-9fcb-685cb2bd9bb5#5a7384a0edeaf27a7ff5b2e2960169fc0554530042c74a5205ddf1cb'))
var step2="Bearer "+step
console.log("step2",step2)
    $.ajax({
    url: baseURL+'api/authorize',
    type: 'POST',
    headers: {
      'Accept': 'application/json',
      'Content-Type': 'application/json',
      'Pre-Authorization': step2
  },

        complete: function(data) {
     console.log('Load was performed.', data);
     console.log(data.responseJSON);
     localStorage.setItem("signInId", data.responseJSON.signInId);
     document.getElementById("myButton").onclick = function () {
        var signInId=localStorage.getItem("signInId")
        var mail=document.getElementById("inputEmail").value
        var password=document.getElementById("inputPassword").value
        console.log(mail)
        let reqObj = {"mail":mail,"password":password,"signInId":signInId}
        console.log(JSON.stringify(reqObj))
        $.ajax({
            url: baseURL+'api/authenticate',
            type: 'POST',
            data: JSON.stringify(reqObj),
            dataType: 'json',
            headers: {
              'Accept': 'application/json',
              'Content-Type': 'application/json'
          },

            success: function(data) {
             console.log(data);
             var code_verifier=localStorage.getItem("codeverif")
             localStorage.setItem("mail", mail);
             var access="Bearer "+utf8_to_b64(data.authCode+'#'+code_verifier)
             $.ajax({
                url: baseURL+'api/oauth/token',
                type: 'GET',
                headers: {
                  'Accept': 'application/json',
                  'Content-Type': 'application/json',
                  'Post-Authorization':access
              },
                success: function(data) {
                 console.log(data);
                 localStorage.setItem("accesstoken", data.accessToken);
                 localStorage.setItem("refreshtoken",data.refreshToken);
                 localStorage.removeItem("signInId");
                 console.log('ok')
                 location.href = "dashboardi.html";
                 
                }
            })
             
            }
            
        })
        
    };
    
    }
})




}
