function coeffs = project_face(avgface, eigfaces, newface)
[r c d] = size(eigfaces);
coeffs = zeros(d, 1);
newface = my_imresize(newface, r, c);
corrected = reshape(newface - avgface, r * c, 1);
for i = 1:d
  coeffs(i) = dot(reshape(eigfaces(:, :, i), 1, r * c), corrected);
end
end

