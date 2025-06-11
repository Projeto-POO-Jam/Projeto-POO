const toastDefaults = {
    toast: true,
    position: 'top-end',
    showConfirmButton: false,
    timer: 2000,
    timerProgressBar: true,
    didOpen: t => {
        t.addEventListener('mouseenter', Swal.stopTimer)
        t.addEventListener('mouseleave', Swal.resumeTimer)
    }
}

export function showSuccess(msg) {
    Swal.fire({ ...toastDefaults, icon: 'success', title: msg })
}

export function showError(msg) {
    Swal.fire({ ...toastDefaults, icon: 'error',   title: msg })
}
