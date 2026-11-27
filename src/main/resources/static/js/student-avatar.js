const avatarFile = document.getElementById('avatar-file');
const avatarImage = document.getElementById('avatar-image');
const avatarDefault = document.getElementById('avatar-default');
const avatarError = document.getElementById('avatar-error');
const savedAvatar = avatarImage.getAttribute('src');
let previewUrl;

function showAvatar(source) {
    avatarImage.hidden = !source;
    if (source) {
        avatarImage.src = source;
    } else {
        avatarImage.removeAttribute('src');
    }
    avatarDefault?.classList.toggle('d-none', Boolean(source));
    avatarDefault?.classList.toggle('d-inline-flex', !source);
}

function rejectAvatar() {
    avatarFile.value = '';
    avatarFile.classList.add('is-invalid');
    avatarError.textContent = 'Chọn ảnh JPG hoặc PNG hợp lệ có dung lượng tối đa 2 MB.';
    showAvatar(savedAvatar);
}

avatarFile.addEventListener('change', () => {
    URL.revokeObjectURL(previewUrl);
    previewUrl = undefined;
    avatarFile.classList.remove('is-invalid');
    avatarError.textContent = '';
    showAvatar(savedAvatar);

    const file = avatarFile.files[0];
    if (!file) return;
    if (file.size > 2 * 1024 * 1024 || !/\.(jpe?g|png)$/i.test(file.name)) {
        rejectAvatar();
        return;
    }

    const url = URL.createObjectURL(file);
    previewUrl = url;
    const preview = new Image();
    preview.onload = () => {
        if (previewUrl === url) showAvatar(url);
    };
    preview.onerror = () => {
        if (previewUrl === url) {
            URL.revokeObjectURL(url);
            previewUrl = undefined;
            rejectAvatar();
        }
    };
    preview.src = url;
});
