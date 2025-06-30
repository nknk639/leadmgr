document.addEventListener('DOMContentLoaded', () => {
  const form = document.getElementById('call-form');
  if (!form) return;
  form.addEventListener('submit', async (e) => {
    e.preventDefault();
    const leadId = form.dataset.leadId;
    const formData = new FormData(form);
    const params = new URLSearchParams();
    for (const [k, v] of formData.entries()) {
      if (v) params.append(k, v);
    }
    const resp = await fetch(`/leads/${leadId}/calls/json`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
      body: params
    });
    if (resp.ok) {
      location.reload();
    } else {
      alert('保存に失敗しました');
    }
  });
});