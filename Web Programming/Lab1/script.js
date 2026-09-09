const pointForm = document.querySelector("#pointForm");
pointForm.addEventListener("submit", function(event) {
    event.preventDefault();
    
    const errorBox = document.getElementById("error");
    errorBox.textContent = "";

    const xChecked = document.querySelector('input[name="x"]:checked');

    if (xChecked === null) {
        errorBox.textContent = "Pick an X value";
        return;
    }

    const x = Number(xChecked.value);



});


