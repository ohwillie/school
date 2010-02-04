function mse = compare_faces(avgface, eigfaces, face1, face2)
coeffs1 = project_face(avgface, eigfaces, face1);
coeffs2 = project_face(avgface, eigfaces, face2);
mean(mean((coeffs1 - coeffs2).^2))
end

