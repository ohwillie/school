function coeffs = project_face(avgface, eigfaces, newface)
[r c d] = size(eigfaces);
coeffs = zeros(d, 1);
corrected = reshape(newface - avgface, r * c, 1);
for i = 1:d
  coeffs(i) = reshape(eigfaces(:, :, i), 1, r * c) * corrected;
end
end

