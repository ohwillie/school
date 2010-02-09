function order = recognize_face(avgface, eigfaces, user_coeffs, face)
[r c d] = size(user_coeffs);
coeffs = project_face(avgface, eigfaces, face);
unsorted = zeros(1, c);
for i = 1:c
  unsorted(i) = my_mse(user_coeffs(:, i), coeffs);
end
[sorted order] = sort(unsorted);
end

