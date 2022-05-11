function edit(el) {
    document.getElementById("title").disabled = false;
    document.getElementById("StartTime").disabled = false;
    document.getElementById("EndTime").disabled = false;
    document.getElementById("Desc").disabled = false;

    let save_btn = document.createElement('a');
    save_btn.setAttribute("onclick", "save(this)");
    save_btn.setAttribute("style", "color: lightskyblue; cursor: pointer");
    const save_txt = document.createElement('small');
    save_txt.textContent = "Save";
    save_btn.appendChild(save_txt);
    el.replaceWith(save_btn);
}

function save(el) {
    document.getElementById("title").disabled = true;
    document.getElementById("StartTime").disabled = true;
    document.getElementById("EndTime").disabled = true;
    document.getElementById("Desc").disabled = true;

    let edit_btn = document.createElement('a');
    edit_btn.setAttribute("onclick", "edit(this)");
    edit_btn.setAttribute("style", "color: lightskyblue; cursor: pointer");
    const edit_txt = document.createElement('small');
    edit_txt.textContent = "Edit";
    edit_btn.appendChild(edit_txt);
    el.replaceWith(edit_btn);
}